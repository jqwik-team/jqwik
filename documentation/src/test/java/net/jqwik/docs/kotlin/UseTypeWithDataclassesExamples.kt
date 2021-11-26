package net.jqwik.docs.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.UseType
import net.jqwik.web.api.Email

class UseTypeWithDataclassesExamples {

    @Property(tries = 10)
    fun generateCommunications(@ForAll @UseType communication: Communication) {
        println(communication)
    }
}


data class Person(val firstName: String, val lastName: String)

data class User(val identity: Person, @Email val email: String)

data class Communication(val from: User, val to: User)