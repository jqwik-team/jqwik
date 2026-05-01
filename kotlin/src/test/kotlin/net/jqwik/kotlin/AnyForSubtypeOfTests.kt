package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.kotlin.api.anyForSubtypeOf
import net.jqwik.testing.TestingSupport
import org.assertj.core.api.Assertions.assertThat
import java.util.*

@PropertyDefaults(tries = 100)
class AnyForSubtypeOfTests {

    sealed interface Interface
    // Make generator Arbitrary<Implementation> cacheable by ensuring Implementation has equals/hashCode
    data class Implementation(val value: String) : Interface

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

    sealed class ParentWithRecursion
    class ChildWithCustomType(val customType: CustomType) : ParentWithRecursion()
    class CustomType(val value: String)

    @Example
    fun `anyForSubtypeOf() with arbitrary recursion`(@ForAll random: Random) {
        val subtypes = anyForSubtypeOf<ParentWithRecursion>(enableArbitraryRecursion = true)
        TestingSupport.checkAllGenerated(subtypes, random) { it is ChildWithCustomType }
    }

    sealed interface ParentInterface
    sealed interface ChildInterface : ParentInterface
    sealed class ChildClass : ParentInterface
    class ChildInterfaceImpl(val value: String) : ChildInterface
    class ChildClassImpl(val value: String) : ChildClass()

    @Provide
    fun parentInterface() = anyForSubtypeOf<ParentInterface>()

    @Property
    fun `anyForSubtypeOf() returns type arbitrary for any concrete subtype of a given sealed class or interface, even nested`(
        @ForAll(
            "parentInterface"
        ) parent: ParentInterface
    ) {
        assertThat(parent).matches { it is ChildInterfaceImpl || it is ChildClassImpl }
    }

    @Example
    fun `anyForSubtypeOf() with type arbitrary customization`(@ForAll random: Random) {
        val subtypes = anyForSubtypeOf<Interface> {
            provide<Implementation> { Arbitraries.of(Implementation("custom arbitrary")) }
        }
        TestingSupport.checkAllGenerated(subtypes, random) { it is Implementation && it.value == "custom arbitrary" }
    }
}