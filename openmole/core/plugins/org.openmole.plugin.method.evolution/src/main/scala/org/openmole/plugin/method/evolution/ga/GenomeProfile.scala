/*
 * Copyright (C) 2014 Romain Reuillon
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

package org.openmole.plugin.method.evolution.ga

import fr.iscpif.mgo._
import org.openmole.plugin.method.evolution._

object GenomeProfile {

  def apply(
    x: Int,
    nX: Int,
    termination: GATermination { type G >: GenomeProfile#G; type P >: GenomeProfile#P; type F >: GenomeProfile#F },
    inputs: Inputs[Double],
    objectives: Objectives,
    reevaluate: Double = 0.0) = {
    val (_x, _nX, _reevaluate, _inputs, _objectives) = (x, nX, reevaluate, inputs, objectives)
    new GenomeProfile {
      val inputs = _inputs
      val objectives = _objectives

      val stateManifest: Manifest[STATE] = termination.stateManifest
      val populationManifest: Manifest[Population[G, P, F]] = implicitly
      val individualManifest: Manifest[Individual[G, P, F]] = implicitly
      val aManifest: Manifest[A] = implicitly
      val fManifest: Manifest[F] = implicitly
      val gManifest: Manifest[G] = implicitly

      val genomeSize = inputs.size
      override val cloneProbability: Double = _reevaluate

      val x = _x
      val nX = _nX
      type STATE = termination.STATE
      def initialState: STATE = termination.initialState
      def terminated(population: Population[G, P, F], terminationState: STATE): (Boolean, STATE) = termination.terminated(population, terminationState)
    }

  }

}

trait GenomeProfile extends GAAlgorithm
    with dynamic.DynamicApplicationGA
    with ProfileRanking
    with BestAggregatedNicheElitism
    with ProfileNiche
    with NoArchive
    with NoDiversity
    with ProfileGenomePlotter
    with BinaryTournamentSelection with selection.ProportionalNumberOfRound
    with TournamentOnRank
    with GeneticBreeding
    with MGFitness
    with MaxAggregation
    with ClampedGenome {
  type INPUT = Double
  def inputConverter = implicitly

  def x: Int
}