package net.jqwik.kotlin

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.array
import net.jqwik.kotlin.api.orNull
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class ArbitraryExtensionsTests {

    @Example
    fun mapCanBeCalledWithoutParentheses(@ForAll random: Random) {
        val strings = String.any().map { s -> s + "42" }
        checkAllGenerated(strings, random) { s -> s.endsWith("42") }
    }

    @Example
    fun filterCanBeCalledWithoutParentheses(@ForAll random: Random) {
        val strings = just("42").filter { s -> s == "42" }
        checkAllGenerated(strings, random) { s -> s.endsWith("42") }
    }

    @Example
    fun flatMapCanBeCalledWithoutParentheses(@ForAll random: Random) {
        val strings = String.any().flatMap { s -> just(s + "42") }
        checkAllGenerated(strings, random) { s -> s.endsWith("42") }
    }

    @Example
    fun arrayFunctionCanBeCalledWithReifiedType(@ForAll random: Random) {
        val stringArray = String.any().array<String, Array<String>>()
        checkAllGenerated(stringArray, random) { array -> array.all { it is String } }
    }

    @Example
    fun arrayFunctionCanBeCalledWithNullableType(@ForAll random: Random) {
        val stringArray = String.any().orNull(0.2).array<String?, Array<String?>>()
        checkAllGenerated(stringArray, random) { array -> array.all { it is String? } }
    }
}