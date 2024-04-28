package it.unibo.alchemist.model.actions

import it.unibo.alchemist.boundary.gps.loaders.TraceLoader
import it.unibo.alchemist.model.* // ktlint-disable no-wildcard-imports
import it.unibo.alchemist.model.maps.GPSTrace
import it.unibo.alchemist.model.maps.MapEnvironment
import it.unibo.alchemist.model.molecules.SimpleMolecule

class TraceNodeStatus<O : RoutingServiceOptions<O>, S : RoutingService<GeoPosition, O>>(
    val environment: MapEnvironment<Any, O, S>,
    node: Node<Any>,
    private val reaction: Reaction<Any>,
    private val path: String,
    private val cycle: Boolean,
    private val normalizer: String,
    vararg normalizerArgs: Any,
) : AbstractAction<Any>(node) {
    companion object {

        private var iterator: MutableIterator<GPSTrace>? = null

        private fun traceFor(
            path: String,
            cycle: Boolean,
            normalizer: String,
            vararg normalizerArgs: Any,
        ): GPSTrace {
            if (iterator == null || !iterator!!.hasNext()) {
                println("Create new trace loader for ${this::class.java}")
                iterator = TraceLoader(path, cycle, normalizer, *normalizerArgs).iterator()
            }
            return if (iterator!!.hasNext()) {
                iterator!!.next()
            } else {
                throw IllegalStateException("All traces have been consumed.")
            }
        }
    }

    private val trace: GPSTrace
    private val status: Molecule = SimpleMolecule("on")

    init {
        trace = traceFor(path, cycle, normalizer, normalizerArgs)
        println("${node.id} -> ${trace.startTime}, ${trace.finalTime}")
    }

    override fun cloneAction(node: Node<Any>, reaction: Reaction<Any>): Action<Any> {
        return TraceNodeStatus(environment, node, reaction, path, cycle, normalizer)
    }

    override fun execute() {
        val time = reaction.tau
        if (time in trace.startTime..trace.finalTime) {
            if (!node.contents.containsKey(status)) node.setConcentration(status, true)
        } else {
            if (node.contents.containsKey(status)) node.removeConcentration(status)
        }
    }

    override fun getContext(): Context {
        return Context.LOCAL
    }
}
