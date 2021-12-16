package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.kotlin.api.anyForType
import net.jqwik.kotlin.api.frequency
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

@PropertyDefaults(tries = 100)
class ConvenienceFunctionsTests {

    data class MyUser(val name: String, val age: Int = -1)

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

}