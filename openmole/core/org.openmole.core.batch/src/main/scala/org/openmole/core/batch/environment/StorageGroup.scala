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

package org.openmole.core.batch.environment

import java.io.File
import org.openmole.misc.tools.service._
import org.openmole.misc.tools.io.FileUtil._
import org.openmole.core.batch.control._
import org.openmole.core.batch.replication._
import scala.annotation.tailrec
import ServiceGroup._

import scala.concurrent.stm._

object StorageGroup extends Logger

import StorageGroup._

class StorageGroup(environment: BatchEnvironment, resources: Iterable[Storage]) extends ServiceGroup with Iterable[Storage] {

  override def iterator = resources.iterator

  def selectAService(usedFiles: Iterable[File]): (Storage, AccessToken) = {
    if (resources.size == 1) {
      val r = resources.head
      return (r, UsageControl.get(r.description).waitAToken)
    }

    val totalFileSize = usedFiles.map { _.size }.sum
    val onStorage = ReplicaCatalog.withClient(ReplicaCatalog.inCatalog(usedFiles, environment.authentication.key)(_))

    def fitness =
      resources.flatMap {
        cur ⇒

          UsageControl.get(cur.description).tryGetToken match {
            case None ⇒ None
            case Some(token) ⇒
              val sizeOnStorage = usedFiles.filter(onStorage.getOrElse(_, Set.empty).contains(cur.description)).map(_.size).sum

              val fitness = orMin(
                StorageControl.qualityControl(cur.description) match {
                  case Some(q) ⇒ math.pow(q.successRate, 2)
                  case None ⇒ 1.
                }) * (if (totalFileSize != 0) (sizeOnStorage.toDouble / totalFileSize) else 1)
              Some((cur, token, fitness))
          }
      }

    @tailrec def selected(value: Double, storages: List[(Storage, AccessToken, Double)]): Option[(Storage, AccessToken)] =
      storages.headOption match {
        case Some((storage, token, fitness)) ⇒
          if (value <= fitness) Some((storage, token))
          else selected(value - fitness, storages.tail)
        case None ⇒ None
      }

    atomic { implicit txn ⇒
      val notLoaded = fitness
      selected(Random.default.nextDouble * notLoaded.map { case (_, _, fitness) ⇒ fitness }.sum, notLoaded.toList) match {
        case Some((storage, token)) ⇒
          for {
            (s, t, _) ← notLoaded
            if (s.description != storage.description)
          } UsageControl.get(s.description).releaseToken(t)
          storage -> token
        case None ⇒ retry
      }
    }

  }
}
