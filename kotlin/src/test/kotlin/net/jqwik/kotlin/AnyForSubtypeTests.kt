package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.kotlin.api.anyForSubtypeOf
import net.jqwik.testing.TestingSupport
import java.util.*

class AnyForSubtypeTests {

    sealed interface Interface
    class Implementation(val value: String) : Interface

    @Example
    fun `anyForSubtypeOf() returns type arbitrary for any implementations of given sealed interface`(@ForAll random: Random) {
        val subtypes = anyForSubtypeOf<Interface>()
        TestingSupport.checkAllGenerated(subtypes, random) { it is Implementation }
    }

    sealed class Parent
    class Child(val value: String) : Parent()

    @Example
    fun `anyForSubtypeOf() returns type arbitrary for any implementations of given sealed class`(@ForAll random: Random) {
        val subtypes = anyForSubtypeOf<Parent>()
        TestingSupport.checkAllGenerated(subtypes, random) { it is Child }
    }
}