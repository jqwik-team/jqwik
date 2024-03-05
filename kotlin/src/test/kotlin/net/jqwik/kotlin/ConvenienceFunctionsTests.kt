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

}