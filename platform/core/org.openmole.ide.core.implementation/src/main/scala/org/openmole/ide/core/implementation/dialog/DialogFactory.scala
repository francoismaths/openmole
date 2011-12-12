/*
 * Copyright (C) 2011 leclaire
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

package org.openmole.ide.core.implementation.dialog

import java.awt.Color
import org.openide.DialogDescriptor
import org.openide.DialogDisplayer
import org.openide.NotifyDescriptor
import org.openmole.ide.core.implementation.control.TopComponentsManager
import org.openmole.ide.core.model.workflow.IMoleScene
import scala.swing.Label

object DialogFactory {
  
  def closeExecutionTab(ms: IMoleScene): Boolean = { 
    if (TopComponentsManager.executionTabs(ms).moleExecution.finished) true
    else if (TopComponentsManager.executionTabs(ms).moleExecution.started){
      val lab = new Label("<html>A simulation is currently running.<br>Close anyway ?</html>"){
        background = Color.white}.peer
      if (DialogDisplayer.getDefault.notify(new DialogDescriptor(lab, "Execution warning")).equals(NotifyDescriptor.OK_OPTION)) true
      else false 
    }
    else true
  }
}
