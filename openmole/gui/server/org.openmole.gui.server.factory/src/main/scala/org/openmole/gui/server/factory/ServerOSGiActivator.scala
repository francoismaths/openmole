package org.openmole.gui.server.factory

/*
 * Copyright (C) 01/10/14 // mathieu.leclaire@openmole.org
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.openmole.gui.ext.data.Data
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

trait ServerOSGiActivator extends BundleActivator {

  // core factories and name of UI factories
  def factories: Seq[(Factory, String)] = Seq()

  abstract override def start(context: BundleContext) = {
    super.start(context)
    factories.foreach { case (factory, uiFactoryName) ⇒ ServerFactories.add(factory.data.getClass, factory, uiFactoryName) }
  }

  abstract override def stop(context: BundleContext) = {
    super.stop(context)
    factories.foreach { case (factory, uiFactoryName) ⇒ ServerFactories.remove(factory.data.getClass) }
  }
}

class OSGiActivator extends BundleActivator {
  def start(context: BundleContext) = {}
  def stop(context: BundleContext) = {}
}