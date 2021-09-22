package net.jqwik.kotlin.api

import net.jqwik.api.*
import net.jqwik.api.statistics.Statistics
import net.jqwik.testing.TestingSupport
import java.util.*
import java.util.function.Predicate

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

    @Property
    fun nullableTypesGetNullInjected(@ForAll aNullableString: String?) {
        Statistics.label("aNullableString is null")
            .collect(aNullableString == null)
            .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 3 }) }
    }

    //@Property
    //fun embeddedNullableTypesGetNullInjected(@ForAll listOfStrings: List<@AlphaChars String?>) {
    //    println(listOfStrings)
    //    //Statistics.label("aNullableString is null")
    //    //    .collect(aNullableString == null)
    //    //    .coverage { coverage -> coverage.check(true).percentage(Predicate { p -> p > 3 }) }
    //}
}