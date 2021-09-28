package net.jqwik.kotlin.api

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport
import net.jqwik.testing.TestingSupport.assertAllGenerated
import java.util.*
import java.util.function.Predicate

class ArbitrariesExtensionsTests {

    @Example
    fun `Arbitraries strings()`(@ForAll random: Random) {
        val stringArbitrary = String.any().ofMaxLength(10)

        assertAllGenerated(
            stringArbitrary.generator(1000),
            random,
            Predicate { s: String? -> s != null  && s.length <= 10}
        )
    }

}