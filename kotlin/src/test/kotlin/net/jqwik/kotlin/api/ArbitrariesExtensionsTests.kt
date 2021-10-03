package net.jqwik.kotlin.api

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class ArbitrariesExtensionsTests {

    @Example
    fun `Arbitraries strings()`(@ForAll random: Random) {
        val stringArbitrary = String.any().ofMaxLength(10)

        checkAllGenerated(
            stringArbitrary.generator(1000),
            random,
            { s: String? -> s != null && s.length <= 10 }
        )
    }

}