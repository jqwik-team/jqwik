package net.jqwik.docs.kotlin

import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.combine
import org.assertj.core.api.Assertions

class CombineExamples {

    @Property
    fun `full names have a space`(@ForAll("fullNames") fullName: String) {
        Assertions.assertThat(fullName).contains(" ")
    }

    @Provide
    fun fullNames() : Arbitrary<String> {
        val firstNames = String.any().alpha().ofMinLength(1)
        val lastNames = String.any().alpha().ofMinLength(1)
        return combine(firstNames, lastNames) {first, last -> "$first $last" }
    }
}