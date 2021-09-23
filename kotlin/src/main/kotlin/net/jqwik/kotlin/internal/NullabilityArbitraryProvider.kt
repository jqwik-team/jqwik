package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.orNull

class NullabilityArbitraryProvider : ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage): Boolean {
        return targetType.isNullable
    }

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<*>> {
        val nonNullType: TypeUsage = targetType.asNotNullable()
        val rawArbitraries = subtypeProvider.apply(nonNullType)
        return rawArbitraries.map { a -> a.orNull(0.05) }.toMutableSet()
    }

    override fun priority(): Int {
        // Replace most providers if type is nullable
        return 101
    }
}