package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.kotlin.api.anySubset
import net.jqwik.kotlin.api.anyValue
import net.jqwik.kotlin.api.ofSize
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class CollectionExtensionsTests {

    @Example
    fun subsetOfSet(@ForAll random: Random) {
        val strings = setOf("one", "two", "three", "four")
        val sets = strings.anySubset().ofSize(1..3)
        checkAllGenerated(sets, random) { s -> s.size in 1..3 }
    }

    @Example
    fun subsetOfList(@ForAll random: Random) {
        val strings = listOf("one", "two", "three", "four")
        val sets = strings.anySubset().ofSize(1..3)
        checkAllGenerated(sets, random) { s -> s.size in 1..3 }
    }

    @Example
    fun chooseAnyOfSet(@ForAll random: Random) {
        val strings = setOf("one", "two", "three", "four")
        val values = strings.anyValue()
        checkAllGenerated(values, random) { s -> s in strings }
    }

    @Example
    fun chooseAnyOfList(@ForAll random: Random) {
        val strings = listOf("one", "two", "three", "four")
        val values = strings.anyValue()
        checkAllGenerated(values, random) { s -> s in strings }
    }

}

