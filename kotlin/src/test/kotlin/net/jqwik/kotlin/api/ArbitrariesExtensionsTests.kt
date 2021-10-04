package net.jqwik.kotlin.api

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class ArbitrariesExtensionsTests {

    @Example
    fun `String any()`(@ForAll random: Random) {
        val any = String.any().ofMaxLength(10)

        checkAllGenerated(
            any.generator(1000),
            random
        ) { value -> value is String && value.length <= 10 }
    }

    @Example
    fun `Char any()`(@ForAll random: Random) {
        val any = Char.any().numeric()

        checkAllGenerated(
            any.generator(1000),
            random
        ) { value -> value is Char && Character.isDigit(value) }
    }

    @Example
    fun `Boolean any()`(@ForAll random: Random) {
        val any = Boolean.any()

        checkAllGenerated(
            any.generator(1000),
            random
        ) { value -> value is Boolean }
    }

}