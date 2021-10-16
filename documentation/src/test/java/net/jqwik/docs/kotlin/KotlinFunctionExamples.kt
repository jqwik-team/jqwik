package net.jqwik.docs.kotlin

import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyFunction2
import org.assertj.core.api.Assertions.assertThat

class KotlinFunctionExamples {

    @Property
    fun generatedFunctionsAreStable(@ForAll aFunc: (String) -> Int) {
        assertThat(aFunc("hello")).isEqualTo(aFunc("hello"))
    }

    @Property
    fun providedFunctionsAreAlsoStable(@ForAll("myIntFuncs") aFunc: (String, String) -> Int) {
        assertThat(aFunc("a", "b")).isBetween(10, 1000)
    }

    @Provide
    fun myIntFuncs(): Arbitrary<(String, String) -> Int> = anyFunction2(Int.any(10..1000))
}