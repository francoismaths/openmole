/*
 *  Copyright (C) 2010 reuillon
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.implementation.observer;

import org.openmole.commons.aspect.eventdispatcher.IObjectChangedSynchronousListener;
import org.openmole.commons.aspect.eventdispatcher.IObjectChangedSynchronousListenerWithArgs;
import org.openmole.commons.exception.InternalProcessingError;
import org.openmole.commons.exception.UserBadDataError;
import org.openmole.commons.tools.structure.Priority;
import org.openmole.core.implementation.internal.Activator;
import org.openmole.core.model.capsule.IGenericTaskCapsule;
import org.openmole.core.model.job.IMoleJob;
import org.openmole.core.model.observer.IVisualizer;

/**
 *
 * @author reuillon
 */
public abstract class Visualizer implements IVisualizer {

   class VisualizerAdapterForMoleJob implements IObjectChangedSynchronousListener<IMoleJob> {

        @Override
        public void objectChanged(IMoleJob job) throws InternalProcessingError, UserBadDataError {
            switch (job.getState()) {
                case COMPLETED:
                    visualize(job.getContext());
                    break;
            }
        }
        
    } 
    
   class VisualizerAdapterForCapsule implements IObjectChangedSynchronousListenerWithArgs<IGenericTaskCapsule> {

        @Override
        public void objectChanged(IGenericTaskCapsule obj, Object[] args) throws InternalProcessingError, UserBadDataError {
            IMoleJob moleJob = (IMoleJob) args[0];
            Activator.getEventDispatcher().registerListener(moleJob, Priority.HIGHEST.getValue(), new VisualizerAdapterForMoleJob());
        }
       
   }
   
   public Visualizer(IGenericTaskCapsule taskCapsule) {
       Activator.getEventDispatcher().registerListener(taskCapsule, Priority.NORMAL.getValue(), new VisualizerAdapterForCapsule());
   }
   
}
