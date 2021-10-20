package net.jqwik.docs.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.StringLength

class InlineClassExamples {

    @Property
    fun test2(@ForAll password: @StringLength(51) SecurePassword) {
        assert(password.length() == 51)
    }

    @JvmInline
    value class SecurePassword(private val s: String) {
        fun length() = s.length
    }
}