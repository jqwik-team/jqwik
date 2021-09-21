package net.jqwik.kotlin

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage

class NullabilityArbitraryProvider:ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage): Boolean {
        val kotlin = targetType.rawType.kotlin
        return false
    }

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<*>> {
        return mutableSetOf()
    }
}