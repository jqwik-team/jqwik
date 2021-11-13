package experiments

import net.jqwik.api.Arbitraries
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide

class KotlinExperiments {
    @Property(tries = 10)
    fun test(@ForAll("users") user: User) {
        println(user)
    }

    @Provide
    fun users() = Arbitraries.forType(User::class.java)

}

data class User(val name: String, val age: Int = -1)
