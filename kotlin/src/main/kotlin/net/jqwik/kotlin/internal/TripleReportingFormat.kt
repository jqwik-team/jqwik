package net.jqwik.kotlin.internal

import net.jqwik.api.SampleReportingFormat
import net.jqwik.api.Tuple

class TripleReportingFormat : SampleReportingFormat {

    override fun appliesTo(value: Any): Boolean {
        return value is Triple<*, *, *>
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun report(value: Any): Any {
        val triple = value as Triple<*, *, *>
        return Tuple.of(triple.first, triple.second, triple.third)
    }
}