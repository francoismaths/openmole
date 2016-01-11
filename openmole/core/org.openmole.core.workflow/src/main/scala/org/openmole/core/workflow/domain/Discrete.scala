/*
 * Copyright (C) 2012 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.workflow.domain

import org.openmole.core.workflow.tools._
import scala.annotation.implicitNotFound
import scala.util.Random

object Discrete {
  implicit def fromStatic[T, D](d: StaticDiscrete[T, D]) = new Discrete[T, D] {
    def iterator(domain: D): FromContext[Iterator[T]] = d.iterator(domain)
  }
}

@implicitNotFound("${D} is not a discrete variation domain of type ${T}")
trait Discrete[+T, -D] extends Domain[T, D] {
  def iterator(domain: D): FromContext[Iterator[T]]
}

@implicitNotFound("${D} is not a static discrete variation domain of type ${T}")
trait StaticDiscrete[+T, -D] extends Domain[T, D] {
  def iterator(domain: D): Iterator[T]
}
