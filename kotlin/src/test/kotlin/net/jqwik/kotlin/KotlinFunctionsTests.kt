package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.Arbitraries.just
import net.jqwik.api.constraints.StringLength
import net.jqwik.kotlin.api.*
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions
import java.util.*

class KotlinFunctionsTests {

    @Example
    fun `anyFunction(FunctionalType)`(@ForAll random: Random) {
        val funcs: Arbitrary<(Int, Int, Int) -> String> =
            anyFunction(Function3::class).returning(String.any().ofLength(5))

        checkAllGenerated(
            funcs,
            random
        ) { func -> func(42, 0, -10).length == 5 }
    }

    @Example
    fun `anyFunction0(returning)`(@ForAll random: Random) {
        val funcs: Arbitrary<() -> String> = anyFunction0(String.any().ofLength(5))
        checkAllGenerated(
            funcs,
            random
        ) { func -> func().length == 5 }
    }

    @Example
    fun `anyFunction1(returning)`(@ForAll random: Random) {
        val funcs: Arbitrary<(Int) -> String> = anyFunction1(String.any().ofLength(5))
        checkAllGenerated(
            funcs,
            random
        ) { func -> func(1).length == 5 }
    }

    @Example
    fun `anyFunction2(returning) to anyFunction4(returning)`(@ForAll random: Random) {
        val funcs2: Arbitrary<(Int, Int) -> String> = anyFunction2(just("2"))
        Assertions.assertThat(funcs2.sample()(1, 2)).isEqualTo("2")

        val funcs3: Arbitrary<(Int, Int, Int) -> String> = anyFunction3(just("3"))
        Assertions.assertThat(funcs3.sample()(1, 2, 3)).isEqualTo("3")

        val funcs4: Arbitrary<(Int, Int, Int, Int) -> String> = anyFunction4(just("4"))
        Assertions.assertThat(funcs4.sample()(1, 2, 3, 4)).isEqualTo("4")
    }

    @Group
    @PropertyDefaults(tries = 10)
    inner class ForAllParameters {

        @Property
        fun noParams(@ForAll aFun: () -> String) {
            Assertions.assertThat(aFun()).isInstanceOf(String::class.java)
        }

        @Property
        fun eightParams(@ForAll aFun: (Int, Int, Int, Int, Int, Int, Int, Int) -> String) {
            Assertions.assertThat(aFun(1, 2, 3, 4, 5, 6, 7, 8)).isInstanceOf(String::class.java)
        }

        @Property
        fun resultTypeWithConstraints(@ForAll aFun: () -> @StringLength(5) String) {
            val result = aFun()
            Assertions.assertThat(result).isInstanceOf(String::class.java)
            Assertions.assertThat(result).hasSize(5)
        }
    }
}