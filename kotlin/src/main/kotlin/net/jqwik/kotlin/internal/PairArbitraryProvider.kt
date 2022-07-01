package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.api.support.CollectorsSupport
import net.jqwik.kotlin.api.anyPair

class PairArbitraryProvider : ArbitraryProvider {

    override fun canProvideFor(targetType: TypeUsage) = targetType.isOfType(Pair::class.java)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<out Any>> {
        val firstType = targetType.getTypeArgument(0)
        val secondType = targetType.getTypeArgument(1)

        return subtypeProvider
            .resolveAndCombine(firstType, secondType)
            .map { arbitraries ->
                val first = arbitraries[0]
                val second = arbitraries[1]
                return@map anyPair(first, second)
            }
            .collect(CollectorsSupport.toLinkedHashSet())
    }
}