/*
 * Copyright (C) 2012 Romain Reuillon
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

package org.openmole.plugin.environment

package object egi {
  private implicit def auth = org.openmole.misc.workspace.Workspace.instance.authenticationProvider

  lazy val complexsystems = EGIEnvironment("vo.complex-systems.eu")
  lazy val biomed = EGIEnvironment("biomed")
  lazy val francegrilles = EGIEnvironment("vo.france-grilles.fr")
}