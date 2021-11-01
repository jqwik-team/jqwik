package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.lifecycle.BeforeProperty
import org.assertj.core.api.Assertions.assertThat

// IntelliJ does not recognize singleton objects as test containers
// See: https://youtrack.jetbrains.com/issue/IDEA-281556
object SingletonTests {

    @BeforeProperty
    fun checkSingleton() {
        assertThat(this).isSameAs(SingletonTests)
    }

    @Example
    fun sample() {
        assertThat(this).isSameAs(SingletonTests)
    }

    @Property(tries = 10)
    fun property(@ForAll s: Int) {
        assertThat(this).isSameAs(SingletonTests)
    }

}
