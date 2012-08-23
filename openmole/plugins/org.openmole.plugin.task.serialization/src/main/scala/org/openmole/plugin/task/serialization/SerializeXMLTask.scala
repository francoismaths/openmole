/*
 * Copyright (C) 2011 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.task.serialization

import java.io.File
import org.openmole.core.implementation.data.Context
import org.openmole.core.implementation.data.Variable
import org.openmole.core.implementation.task.Task
import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.data.IContext
import org.openmole.core.model.task.IPluginSet
import org.openmole.core.serializer.SerializerService
import org.openmole.misc.workspace.Workspace

object SerializeXMLTask {

  def apply(
    name: String)(implicit plugins: IPluginSet) =
    new SerializeXMLTaskBuilder { builder ⇒

      def toTask = new SerializeXMLTask(name, builder.serialize) {
        val inputs = builder.inputs
        val outputs = builder.outputs
        val parameters = builder.parameters
      }

    }

}

sealed abstract class SerializeXMLTask(
    val name: String,
    serialize: List[(IPrototype[_], IPrototype[File])])(implicit val plugins: IPluginSet) extends Task {

  override def process(context: IContext) =
    Context.empty ++ serialize.map {
      case (input, output) ⇒
        val file = Workspace.newFile
        SerializerService.serialize(context.value(input).get, file)
        new Variable(output, file)
    }

}
