package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.AlphaChars
import net.jqwik.api.constraints.StringLength
import net.jqwik.api.constraints.UseType
import net.jqwik.testing.SuppressLogging

@SuppressLogging
class UseTypeWithDataAndValueClassesTests {

    @Property(tries = 100)
    fun personsWillBeGeneratedAccordingToConstraints(@ForAll @UseType person: Person) {
        assert(person is Person)
        person.firstName?.also { name ->
            assert(name.value.isNotEmpty())
            assert(name.value.length <= 20)
        }
        assert(person.lastName.value.isNotEmpty())
        assert(person.lastName.value.length <= 20)
    }
}

data class Person(val firstName: Name?, val lastName: Name, val age: Age)

data class Name(val value: @StringLength(min = 1, max = 20) @AlphaChars String) {
    override fun toString() = value
}

@JvmInline
value class Age(val value: Int) {
    // This is ignored since Java only sees the inlined type
    init {
        require(value >= 0)
    }
}
