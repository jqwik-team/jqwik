package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.Group
import net.jqwik.testing.TestingSupport
import org.assertj.core.api.Assertions.assertThat

class ReportingFormatsTests {

    @Group
    inner class Pairs {

        @Example
        fun singleLineReport() {
            val pair = Pair(1, "hallo")
            assertThat(TestingSupport.singleLineReport(pair)).isEqualTo("(1, \"hallo\")")
        }

        @Example
        fun multiLineReport() {
            val pair = Pair(1, "hallo")
            assertThat(TestingSupport.multiLineReport(pair)).containsExactly(
                "(",
                "  1,",
                "  \"hallo\"",
                ")"
            )
        }
    }

    @Group
    inner class Triples {

        @Example
        fun singleLineReport() {
            val triple = Triple(1, "hallo", true)
            assertThat(TestingSupport.singleLineReport(triple)).isEqualTo("(1, \"hallo\", true)")
        }

        @Example
        fun multiLineReport() {
            val triple = Triple(1, "hallo", true)
            assertThat(TestingSupport.multiLineReport(triple)).containsExactly(
                "(",
                "  1,",
                "  \"hallo\",",
                "  true",
                ")"
            )
        }
    }
}