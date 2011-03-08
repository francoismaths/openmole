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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.implementation.transition

import org.openmole.misc.exception.InternalProcessingError
import org.openmole.core.implementation.capsule.GenericCapsule._
import org.openmole.core.implementation.tools.ContextAggregator._
import org.openmole.core.model.capsule.IGenericCapsule
import org.openmole.core.model.data.IContext
import org.openmole.core.model.data.IVariable
import org.openmole.core.model.job.ITicket
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.mole.ISubMoleExecution
import org.openmole.core.model.tools.IContextBuffer
import org.openmole.core.model.transition.ICondition
import org.openmole.core.model.transition.IGenericTransition
import org.openmole.core.model.transition.ISlot
import org.openmole.core.implementation.tools.ToArrayFinder._

abstract class GenericTransition(val start: IGenericCapsule, val end: ISlot, val condition: ICondition, val filtered: Set[String]) extends IGenericTransition {

  plugStart 
  end += this

  def nextTaskReady(ticket: ITicket, execution: IMoleExecution): Boolean = {
    val registry = execution.localCommunication.transitionRegistry
    !end.transitions.exists(!registry.isRegistred(_, ticket))
  }

  protected def submitNextJobsIfReady(context: IContextBuffer, ticket: ITicket, moleExecution: IMoleExecution, subMole: ISubMoleExecution) = synchronized {
    val registry = moleExecution.localCommunication.transitionRegistry
    registry.register(this, ticket, context)
 
    if (nextTaskReady(ticket, moleExecution)) {
      val combinaison = end.transitions.flatMap(registry.remove(_, ticket).get).map{_.toVariable} ++ 
                        end.capsule.inputDataChannels.flatMap{_.consums(ticket, moleExecution)}
      
      val newTicket = 
        if (end.capsule.intputSlots.size <= 1) ticket 
        else moleExecution.nextTicket(ticket.parent.getOrElse(throw new InternalProcessingError("BUG should never reach root ticket")))

      val toAggregate = combinaison.groupBy(_.prototype.name)
      
      val toArray = toArrayManifests(end.capsule)      
      val newContext = aggregate(end.capsule.userInputs, toArray, combinaison)
      moleExecution.submit(end.capsule, newContext, newTicket, subMole)
    }
  }

  override def perform(context: IContext, ticket: ITicket, toClone: Set[String], scheduler: IMoleExecution, subMole: ISubMoleExecution) = {
    if (isConditionTrue(context)) {
      /*-- Remove filtred --*/
      for(name <- filtered) context -= name
      performImpl(context, ticket, toClone, scheduler, subMole);
    }
  }


  override def isConditionTrue(context: IContext): Boolean = condition.evaluate(context)

  protected def performImpl(context: IContext, ticket: ITicket, toClone: Set[String], scheduler: IMoleExecution, subMole: ISubMoleExecution) 
  protected def plugStart
}
