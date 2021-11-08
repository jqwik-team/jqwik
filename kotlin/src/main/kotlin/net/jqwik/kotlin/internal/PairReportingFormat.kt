package net.jqwik.kotlin.internal

import net.jqwik.api.SampleReportingFormat
import net.jqwik.api.Tuple

class PairReportingFormat : SampleReportingFormat {

    override fun appliesTo(value: Any): Boolean {
        return value is Pair<*, *>
    }

    override fun report(value: Any): Any {
        val pair = value as Pair<*, *>
        return Tuple.of(pair.first, pair.second)
    }
}