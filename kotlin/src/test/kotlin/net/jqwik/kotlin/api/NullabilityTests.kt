package net.jqwik.kotlin.api

import net.jqwik.api.*
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

    @Example
    fun test(@ForAll aString : String, @ForAll aNullableString: String?) {
        val parameters = NullabilityTests::test.parameters
        for (parameter in parameters) {
            println(parameter.type.isMarkedNullable)
        }
    }
}