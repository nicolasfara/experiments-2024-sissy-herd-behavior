package it.unibo.alchemist.boundary.extractors

import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Time

class PositionExporter(nodesCount: Int) : AbstractDoubleExporter() {
    override val columnNames: List<String> = (0 until nodesCount).flatMap { listOf("node-$it[x]", "node-$it[y]") }

    override fun <T> extractData(
        environment: Environment<T, *>,
        reaction: Actionable<T>?,
        time: Time,
        step: Long
    ): Map<String, Double> = environment.nodes.flatMap {
        listOf(
            "node-${it.id}[x]" to environment.getPosition(it).coordinates[0],
            "node-${it.id}[y]" to environment.getPosition(it).coordinates[1]
        )
    }.toMap()
}