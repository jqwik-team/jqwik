package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport
import java.util.*

class NullabilityTests {
    @Example
    fun `inject null makes result type nullable`(@ForAll random: Random) {
        val stringArbitrary = Arbitraries.strings()
        val nullableArbitrary: Arbitrary<String?> = stringArbitrary.injectNull(0.5)

        TestingSupport.assertAtLeastOneGenerated(
            nullableArbitrary.generator(1000),
            random
        ) { s: String? -> s != null }

        TestingSupport.assertAtLeastOneGenerated(
            nullableArbitrary.generator(1000),
            random
        ) { s: String? -> s == null }
    }
}