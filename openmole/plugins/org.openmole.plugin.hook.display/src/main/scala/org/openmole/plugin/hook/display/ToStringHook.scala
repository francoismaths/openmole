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

package org.openmole.plugin.hook.display

import java.io.PrintStream
import org.openmole.core.implementation.data.DataSet
import org.openmole.core.implementation.hook.CapsuleExecutionHook
import org.openmole.core.model.mole.ICapsule
import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.data.IVariable
import org.openmole.core.model.job.IMoleJob
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.misc.tools.io.Prettifier._

class ToStringHook(execution: IMoleExecution, capsule: ICapsule, out: PrintStream, prototypes: IPrototype[_]*) extends CapsuleExecutionHook(execution, capsule) {

  def this(execution: IMoleExecution, capsule: ICapsule, prototypes: IPrototype[_]*) = this(execution, capsule, System.out, prototypes: _*)

  override def process(moleJob: IMoleJob) = {
    import moleJob.context

    if (!prototypes.isEmpty)
      out.println(
        prototypes.map(p ⇒ p -> context.variable(p)).map {
          _ match {
            case (p, Some(v)) ⇒ v
            case (p, None) ⇒ p.name + " not found"
          }
        }.mkString(","))
    else out.println(context.values.mkString(", "))
  }

  def inputs = DataSet(prototypes)

}
