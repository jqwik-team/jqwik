package net.jqwik.kotlin.api

import net.jqwik.api.*
import net.jqwik.api.constraints.WithNull
import net.jqwik.api.statistics.Statistics
import net.jqwik.api.statistics.StatisticsReport
import net.jqwik.testing.TestingSupport
import java.util.*
import java.util.function.Predicate

@StatisticsReport(onFailureOnly = true)
class NullabilityTests {

    @Example
    fun `orNull() makes result type nullable`(@ForAll random: Random) {
        val stringArbitrary: Arbitrary<String> = Arbitraries.strings()
        val nullableArbitrary: Arbitrary<String?> = stringArbitrary.orNull(0.5)

        TestingSupport.assertAtLeastOneGenerated(
            nullableArbitrary.generator(1000),
            random
        ) { s: String? -> s != null }

        TestingSupport.assertAtLeastOneGenerated(
            nullableArbitrary.generator(1000),
            random
        ) { s: String? -> s == null }
    }

    @Property(edgeCases = EdgeCasesMode.NONE)
    fun nullableTypesGetNullInjected(@ForAll aNullableString: String?) {
        Statistics.label("aNullableString is null")
            .collect(aNullableString == null)
            .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 3 }) }
    }

    @Property(edgeCases = EdgeCasesMode.NONE)
    fun nullableTypesGetNullInjectedWithCustomProbability(@ForAll @WithNull(0.5) aNullableString: String?) {
        Statistics.label("aNullableString is null")
            .collect(aNullableString == null)
            .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 40 }) }
    }

}