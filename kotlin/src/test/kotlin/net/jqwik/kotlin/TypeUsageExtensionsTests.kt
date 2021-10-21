package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.kotlinType

class TypeUsageExtensionsTests {

    @Example
    fun kTypeIsAddedToTypeUsageOnTopLevel(@ForAll("ints") anInt: Int) {
        assert(anInt == 42)
    }

    @Provide
    fun ints(type: TypeUsage): Arbitrary<Int> {
        assert(type.kotlinType != null)
        return Arbitraries.just(42)
    }
}