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

package org.openmole.core.updater

import java.util.concurrent.Executors
import org.openmole.tool.thread._

import scala.concurrent.duration.FiniteDuration

object Updater {

  private lazy val scheduler = Executors.newScheduledThreadPool(1, daemonThreadFactory)
  private lazy val pool = Executors.newCachedThreadPool(daemonThreadFactory)

  def registerForUpdate(updatable: IUpdatableWithVariableDelay) = {
    val task = new UpdaterTask(updatable)
    pool.submit(task)
  }

  def delay(updatable: IUpdatableWithVariableDelay): Unit = {
    val task = new UpdaterTask(updatable)
    delay(task)
  }

  def registerForUpdate(updatable: IUpdatable, updateInterval: FiniteDuration): Unit = {
    registerForUpdate(new UpdatableWithFixedDelay(updatable, updateInterval))
  }

  def delay(updatable: IUpdatable, updateInterval: FiniteDuration): Unit = {
    delay(new UpdatableWithFixedDelay(updatable, updateInterval))
  }

  def delay(updaterTask: UpdaterTask) = {
    scheduler.schedule(
      new Runnable {
        override def run = pool.submit(updaterTask)
      }, updaterTask.updatable.delay.length, updaterTask.updatable.delay.unit)
  }

}
