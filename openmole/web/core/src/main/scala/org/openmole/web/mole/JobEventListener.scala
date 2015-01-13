package org.openmole.web.mole

import org.openmole.misc.eventdispatcher.{ Event, EventListener }
import org.openmole.core.workflow.mole.MoleExecution
import org.openmole.core.workflow.mole.MoleExecution.{ Finished, Starting, JobCreated, JobStatusChanged }
import org.openmole.web.db.tables.MoleStats
import org.openmole.web.cache.{ Stats, Status, DataHandler }
import org.openmole.core.workflow.job

/**
 * Created with IntelliJ IDEA.
 * User: luft
 * Date: 6/14/13
 * Time: 1:34 PM
 */
class JobEventListener(mH: MoleHandling) extends EventListener[MoleExecution] {

  override def triggered(execution: MoleExecution, event: Event[MoleExecution]) = {
    val stats = mH.getMoleStats(execution)

    def state2Lens(s: job.State.State, stats: Stats) = {
      import job.State._
      println(s)
      s match {
        case READY     ⇒ stats.lens.ready
        case RUNNING   ⇒ stats.lens.running
        case COMPLETED ⇒ stats.lens.completed
        case CANCELED  ⇒ stats.lens.cancelled
        case FAILED    ⇒ stats.lens.failed
        case _         ⇒ throw new Exception(s"Unknown job status: $s")
      }
    }

    event match {
      case x: JobCreated       ⇒ mH.updateStats(execution, stats.lens.ready++)
      case x: JobStatusChanged ⇒ mH.updateStats(execution, state2Lens(x.oldState, state2Lens(x.newState, stats)++)--)
    }
  }
}

class MoleStatusListener(mH: MoleHandling) extends EventListener[MoleExecution] {
  override def triggered(execution: MoleExecution, event: Event[MoleExecution]) = {
    event match {
      case x: Starting ⇒ mH.setStatus(execution, Status.Running)
      case x: Finished ⇒ {
        mH.setStatus(execution, Status.Finished)
        mH.storeResultBlob(execution)
        mH.decacheMole(execution)
      }
    }
  }
}