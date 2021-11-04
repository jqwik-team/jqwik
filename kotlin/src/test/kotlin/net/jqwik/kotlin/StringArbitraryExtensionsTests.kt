package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.arbitraries.StringArbitrary
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.ofLength
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class StringArbitraryExtensionsTests {

    @Example
    fun `StringArbitrary ofLength() with range`(@ForAll random: Random) {
        val stringArbitrary: StringArbitrary = String.any().ofLength(2..12)
        checkAllGenerated(stringArbitrary, random) { string -> string.length in 2..12 }
    }

}