package net.jqwik.kotlin

import net.jqwik.api.Arbitraries
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.anyForSubtypeOf
import net.jqwik.kotlin.api.anyForType
import net.jqwik.kotlin.api.frequency
import net.jqwik.kotlin.api.frequencyOf
import net.jqwik.testing.SuppressLogging
import net.jqwik.testing.TestingSupport
import org.assertj.core.api.Assertions.assertThat
import java.util.*

@PropertyDefaults(tries = 100)
class ConvenienceFunctionsTests {

    data class MyUser(val name: String, val age: Int = -1)

    @SuppressLogging("edge case generation exceeded warning")
    @Property
    fun anyForType(@ForAll("users") user: MyUser) {
        assertThat(user.name is String).isTrue
        assertThat(user.age is Int).isTrue
    }

    @Provide
    fun users() = anyForType<MyUser>()

    @Property
    fun frequencyWithTuple(@ForAll("frequencies") anInt: Int) {
        assertThat(anInt).isIn(111, 222, 333)
    }

    @Provide
    fun frequencies() = frequency(
        Pair(1, 111),
        Pair(2, 222),
        Pair(3, 333),
        Pair(0, 99999)
    )

    @Property
    fun frequencyOfWithTuple(@ForAll("frequenciesOf") anInt: Int) {
        assertThat(anInt).isIn(111, 222, 333)
    }

    @Provide
    fun frequenciesOf() = frequencyOf(
        Pair(1, Arbitraries.just(111)),
        Pair(2, Arbitraries.just(222)),
        Pair(3, Arbitraries.just(333)),
        Pair(0, Arbitraries.just(99999))
    )


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