package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.constraints.WithNull
import net.jqwik.api.statistics.Statistics
import net.jqwik.api.statistics.StatisticsReport
import net.jqwik.kotlin.api.orNull
import net.jqwik.testing.TestingSupport.checkAtLeastOneGenerated
import java.util.*
import java.util.function.Predicate

@StatisticsReport(onFailureOnly = true)
class NullabilityTests {

    @Example
    fun `orNull() makes result type nullable`(@ForAll random: Random) {
        val stringArbitrary: Arbitrary<String> = Arbitraries.strings()
        val nullableArbitrary: Arbitrary<String?> = stringArbitrary.orNull(0.5)

        checkAtLeastOneGenerated(
            nullableArbitrary,
            random
        ) { s: String? -> s != null }

        checkAtLeastOneGenerated(
            nullableArbitrary,
            random
        ) { s: String? -> s == null }
    }

    @Property(tries = 2000, edgeCases = EdgeCasesMode.NONE)
    fun nullableTypesGetNullInjected(@ForAll aNullableString: String?) {
        Statistics.label("aNullableString is null")
            .collect(aNullableString == null)
            .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 3 }) }
    }

    @Property(tries = 2000, edgeCases = EdgeCasesMode.NONE)
    fun nullableTypesGetNullInjectedWithCustomProbability(@ForAll @WithNull(0.5) aNullableString: String?) {
        Statistics.label("aNullableString is null")
            .collect(aNullableString == null)
            .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 40 }) }
    }

}