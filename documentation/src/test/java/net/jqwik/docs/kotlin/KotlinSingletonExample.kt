package net.jqwik.docs.kotlin

import net.jqwik.api.Example
import org.assertj.core.api.Assertions

// One of the examples will fail
object KotlinSingletonExample {

    var lastExample: String = ""

    @Example
    fun example1() {
        Assertions.assertThat(lastExample).isEmpty()
        lastExample = "example1"
    }

    @Example
    fun example2() {
        Assertions.assertThat(lastExample).isEmpty()
        lastExample = "example2"
    }
}