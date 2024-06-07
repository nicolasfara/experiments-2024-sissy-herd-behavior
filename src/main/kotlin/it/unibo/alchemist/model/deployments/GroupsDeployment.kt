package it.unibo.alchemist.model.deployments

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Position
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.FastMath

/**
 * Allows deploy nodes in multiple groups.
 * @param environment
 * @param randomGenerator
 * @param nodeCount total number of nodes.
 * @param centerX x coordinate of the deployment zone center.
 * @param centerY y coordinate of the deployment zone center.
 * @param radius of the circle where all groups will be deployed.
 * @param groupNumber number of groups.
 * @param groupRadius radius of the circle where nodes from the same group will be deployed.
 */
class GroupsDeployment<P : Position<P>>(
    environment: Environment<*, P>,
    randomGenerator: RandomGenerator,
    nodeCount: Int,
    private val centerX: Double,
    private val centerY: Double,
    private val radius: Double,
    private val groupNumber: Int,
    private val groupRadius: Double,
) : AbstractRandomDeployment<P>(environment, randomGenerator, nodeCount) {
    private val groupRandomValuesMap = mutableMapOf<Int, P>()
    private val alreadyComputedPositions: MutableSet<P> = mutableSetOf()

    override fun indexToPosition(i: Int): P {
        val groupIndex = i % groupNumber
        if (!groupRandomValuesMap.containsKey(groupIndex)) {
            val groupAngle = randomDouble(0.0, 2 * FastMath.PI)
            val groupRad: Double = radius * FastMath.sqrt(randomDouble())
            val groupCenter = makePosition(
                centerX + groupRad * FastMath.cos(groupAngle),
                centerY + groupRad * FastMath.sin(groupAngle),
            )
            groupRandomValuesMap[groupIndex] = groupCenter
        }
        val groupCenter = groupRandomValuesMap.getOrDefault(groupIndex, makePosition(0, 0)).coordinates

        val angle = randomDouble(0.0, 2 * FastMath.PI)
        val rad: Double = groupRadius * FastMath.sqrt(randomDouble())
        var candidatePosition = makePosition(groupCenter[0] + rad * FastMath.cos(angle), groupCenter[1] + rad * FastMath.sin(angle))
        // Generate a position not colliding with already computed positions
        while (alreadyComputedPositions.any { isColliding(candidatePosition) }) {
            val newAngle = randomDouble(0.0, 2 * FastMath.PI)
            val newRad: Double = groupRadius * FastMath.sqrt(randomDouble())
            candidatePosition = makePosition(groupCenter[0] + newRad * FastMath.cos(newAngle), groupCenter[1] + newRad * FastMath.sin(newAngle))
        }
        alreadyComputedPositions.add(candidatePosition)
        return candidatePosition
    }

    private fun isColliding(other: P): Boolean {
        return alreadyComputedPositions.any { it.distanceTo(other) < 3.0 }
    }
}
