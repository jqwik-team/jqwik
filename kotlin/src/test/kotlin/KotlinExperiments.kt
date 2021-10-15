import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property

class KotlinExperiments {

    fun myFun( aFun: (String) -> Int) {
        aFun("hello")
    }

    @Property(tries = 10)
    fun test(@ForAll myFun: (String, Int) -> Int) {
        println("hello: " + myFun("hello", 3))
        println("oopss: " + myFun("oopss", 4))
    }

    @Example
    fun testing() {
        val type = this::myFun.parameters[0].type
        println(type)
    }
}