package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.jvm.kotlinFunction

class NullabilityTests {
    @Example
    fun `inject null makes result type nullable`(@ForAll random: Random) {
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

    @Example
    fun test(@ForAll aString: String, @ForAll aNullableString: String?) {
        val m: Method = NullabilityTests::class.java.getMethod("test", String::class.java, String::class.java)

        val testFunction = m.kotlinFunction!!

        //val testFunction =
        //    Class.forName("net.jqwik.kotlin.api.NullabilityTests").kotlin.declaredFunctions
        //        .filter { f -> f.name == "test" }
        //        .first()
        val parameters = testFunction.parameters
        for (parameter in parameters) {
            println(parameter.type.isMarkedNullable)
        }
    }
}