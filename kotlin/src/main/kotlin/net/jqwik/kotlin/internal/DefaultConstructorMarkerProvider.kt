package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import kotlin.jvm.internal.DefaultConstructorMarker

class DefaultConstructorMarkerProvider: ArbitraryProvider {

    override fun canProvideFor(targetType: TypeUsage) = targetType.isOfType(DefaultConstructorMarker::class.java)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<out Any>> {
        return mutableSetOf(Arbitraries.nothing())
    }
}