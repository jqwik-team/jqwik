package net.jqwik.kotlin.api

import net.jqwik.api.*
import net.jqwik.api.constraints.StringLength
import net.jqwik.testing.TestingSupport
import org.assertj.core.api.Assertions
import java.util.*

@PropertyDefaults(tries = 10)
class KotlinFunctionsTests {

    @Example
    fun anyFunction3(@ForAll random: Random) {
        val funcs: Arbitrary<(Int, Int, Int) -> String> =
            anyFunction(Function3::class).returning(String.any().ofLength(5))

        TestingSupport.checkAllGenerated(
            funcs,
            random
        ) { func -> func(42, 0, -10).length == 5 }
    }

    @Property
    fun noParams(@ForAll aFun: () -> String) {
        Assertions.assertThat(aFun()).isInstanceOf(String::class.java)
    }

    @Property
    fun resultTypeWithConstraints(@ForAll aFun: () -> @StringLength(5) String) {
        val result = aFun()
        Assertions.assertThat(result).isInstanceOf(String::class.java)
        Assertions.assertThat(result).hasSize(5)
    }
}