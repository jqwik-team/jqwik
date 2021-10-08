package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Example
import net.jqwik.api.ForAll
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
}