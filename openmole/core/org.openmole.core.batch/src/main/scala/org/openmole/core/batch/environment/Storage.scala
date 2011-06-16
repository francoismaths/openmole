/*
 * Copyright (C) 2010 reuillon
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

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import org.openmole.misc.tools.service.Logger
import org.openmole.misc.exception.InternalProcessingError
import org.openmole.misc.tools.service.RNG
import org.openmole.core.batch.control.AccessToken
import org.openmole.core.batch.control.StorageControl
import org.openmole.core.batch.control.StorageDescription
import org.openmole.core.batch.control.QualityControl
import org.openmole.core.batch.control.UsageControl
import org.openmole.core.batch.file.RelativePath
import org.openmole.core.batch.file.URIFile
import org.openmole.core.batch.file.URIFileCleaner
import org.openmole.core.batch.file.GZURIFile
import org.openmole.core.batch.file.IURIFile
import org.openmole.core.batch.replication.ReplicaCatalog
import org.openmole.misc.executorservice.ExecutorService
import org.openmole.misc.executorservice.ExecutorType
import org.openmole.misc.workspace.ConfigurationLocation

import org.openmole.misc.workspace.Workspace
import scala.collection.JavaConversions._

object Storage extends Logger

abstract class Storage(environment: BatchEnvironment, val URI: URI, nbAccess: Int) extends BatchService(environment) {
    
  @transient lazy val description = new StorageDescription(URI)
  
  StorageControl.registerRessouce(description, UsageControl(nbAccess), new QualityControl(Workspace.preferenceAsInt(BatchEnvironment.QualityHysteresis)))      

  import Storage._
  
  @transient protected var baseSpaceVar: IURIFile = null

  def persistentSpace(token: AccessToken): IURIFile 
  def tmpSpace(token: AccessToken): IURIFile
  def baseDir(token: AccessToken): IURIFile
  def root = new URI(URI.getScheme + "://" +  URI.getAuthority)
  def resolve(path: String) = root.resolve(path)
 
  implicit def stringDecorator(path: String) = new RelativePath(root).stringDecorator(path)
  
  def test: Boolean = {
    try {
      val token = StorageControl.usageControl(description).waitAToken

      try {
        val lenght = 10

        val rdm = new Array[Byte](lenght)

        RNG.nextBytes(rdm)

        val testFile = tmpSpace(token).newFileInDir("test", ".bin")
        val tmpFile = Workspace.newFile("test", ".bin")

        try {
          //BufferedWriter writter = new BufferedWriter(new FileWriter(tmpFile));
          val output = new FileOutputStream(tmpFile)
          try output.write(rdm)
          finally output.close

          URIFile.copy(tmpFile, testFile, token)
        } finally tmpFile.delete

        try {
          val local = testFile.cache(token)
          val input = new FileInputStream(local)
          val resRdm = new Array[Byte](lenght)
        
          val nb = try input.read(resRdm) finally input.close
          
          //String tmp = read.readLine();
          if (nb == lenght && rdm.deep == resRdm.deep) return true
          
        } finally ExecutorService.executorService(ExecutorType.REMOVE).submit(new URIFileCleaner(testFile, false))
      } finally StorageControl.usageControl(description).releaseToken(token)
    } catch {
      case e => logger.log(FINE, URI.toString, e)
    }
    return false
  }

  override def toString: String = URI.toString
}
