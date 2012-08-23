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

package org.openmole.core.implementation.sampling

import org.openmole.core.implementation.data.Prototype._
import org.openmole.core.implementation.data.DataSet
import org.openmole.core.model.data.IDataSet
import org.openmole.core.model.sampling.ISampling

trait Sampling extends ISampling {
  override def inputs: IDataSet = DataSet.empty
}
