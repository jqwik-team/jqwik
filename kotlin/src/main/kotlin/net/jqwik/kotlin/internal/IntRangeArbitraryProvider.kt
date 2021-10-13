package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.IntRangeArbitrary

class IntRangeArbitraryProvider: ArbitraryProvider {

    override fun canProvideFor(targetType: TypeUsage) = targetType.isOfType(IntRange::class.java)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<out Any>> {
        return mutableSetOf(IntRangeArbitrary())
    }
}