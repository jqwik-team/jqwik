package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property

// IntelliJ does not recognize singleton objects as test containers
// See: https://youtrack.jetbrains.com/issue/IDEA-281556
object SingletonTests {

    @Example
    fun sample() {
        assert(this == SingletonTests)
    }

    @Property(tries = 10)
    fun property(@ForAll s: Int) {
        assert(this == SingletonTests)
    }
}