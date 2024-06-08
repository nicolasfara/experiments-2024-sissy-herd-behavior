package it.unibo.alchemist.boundary.extractors

import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.GeoPosition
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.alchemist.model.maps.environments.OSMEnvironment
import it.unibo.alchemist.model.molecules.SimpleMolecule
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class SpeedExtractorOSME(private val stepInSeconds: Int) : AbstractDoubleExporter() {
    override val columnNames: List<String> = listOf("speed")
    private var previousSnapshot: List<NodeSnapshot> = emptyList()

    override fun <T> extractData(
        environment: Environment<T, *>,
        reaction: Actionable<T>?,
        time: Time,
        step: Long
    ): Map<String, Double> {
        require(environment is OSMEnvironment)
        val zebrasPositions = environment.nodes
            .filter { it.contains(SimpleMolecule("on")) }
            .map { NodeSnapshot(it.id, environment.getPosition(it)) }

        val velocities = zebrasPositions.associate { (id, position) ->
            val previousPosition = previousSnapshot.firstOrNull { it.id == id }?.position ?: position
            val distance = previousPosition.distanceTo(position)
            val velocity = (distance / stepInSeconds) * 3.6
            id.toString() to velocity + 1
        }
        previousSnapshot = zebrasPositions
        return velocities
    }
}
