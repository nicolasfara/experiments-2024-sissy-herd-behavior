package it.unibo.alchemist.boundary.extractors

import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Time
import it.unibo.alchemist.model.molecules.SimpleMolecule

class SpeedExtractor : AbstractDoubleExporter() {
    override val columnNames: List<String> = (0..139).map { "node-$it" }

    override fun <T> extractData(
        environment: Environment<T, *>,
        reaction: Actionable<T>?,
        time: Time,
        step: Long
    ): Map<String, Double> {
        return environment.nodes.associate {
            columnNames[it.id] to it.getConcentration(SimpleMolecule("speed")) as Double
        }
    }
}
