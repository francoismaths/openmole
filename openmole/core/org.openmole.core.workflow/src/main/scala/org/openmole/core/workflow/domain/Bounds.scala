/*
 * Copyright (C) 2010 Romain Reuillon
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

package org.openmole.core.workflow.domain

import org.openmole.core.workflow.data._
import org.openmole.core.workflow.tools.FromContext

import scala.annotation.implicitNotFound

object Bounds {

  implicit def staticBondsIsBounds[T, D](implicit staticBounds: StaticBounds[T, D]) = new Bounds[T, D] {
    override def min(domain: D): FromContext[T] = staticBounds.min(domain)
    override def max(domain: D): FromContext[T] = staticBounds.max(domain)
  }

}

@implicitNotFound("${D} is not a bounded variation domain of type ${T}")
trait Bounds[+T, -D] extends Domain[T, D] {
  def min(domain: D): FromContext[T]
  def max(domain: D): FromContext[T]
}


@implicitNotFound("${D} is not a static bounded variation domain of type ${T}")
trait StaticBounds[+T, -D] extends Domain[T, D] {
  def min(domain: D): T
  def max(domain: D): T
}
