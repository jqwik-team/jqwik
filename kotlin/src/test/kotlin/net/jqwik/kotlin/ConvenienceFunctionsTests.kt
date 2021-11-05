package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.anyForType
import org.assertj.core.api.Assertions

@PropertyDefaults(tries = 100)
class ConvenienceFunctionsTests {

    data class MyUser(val name: String, val age: Int = -1)

    @Property
    fun anyForType(@ForAll("users") user: MyUser) {
        Assertions.assertThat(user.name is String).isTrue
        Assertions.assertThat(user.age is Int).isTrue
    }

    @Provide
    fun users() = anyForType<MyUser>()

}