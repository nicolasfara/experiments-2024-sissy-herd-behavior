package it.unibo.alchemist.boundary.extractors

/**
 *
 */
abstract class AbstractRangeExtractor(private val slots: Int = 20, private val maxValue: Double) : AbstractDoubleExporter() {
    private val ranges by lazy {
        val step = maxValue / slots
        (0 until slots).map {
            val start = it * step
            val end = start + step
            start..end
        }
    }
    private val rangesString = ranges.map { "${it.start}-${it.endInclusive}" }
    override val columnNames: List<String> = rangesString

    /**
     * @param value
     * @return the group name
     */
    fun getRange(value: Double): String {
        // Check if the velocity is in one of the ranges
        val selectedRange = ranges.indexOfFirst { value in it }
        return rangesString.getOrNull(selectedRange) ?: error("Value $value not in any range")
    }
}
