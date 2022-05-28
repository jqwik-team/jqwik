package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.anyPair
import net.jqwik.kotlin.api.anyTriple
import java.util.stream.Collectors

class TripleArbitraryProvider : ArbitraryProvider {

    override fun canProvideFor(targetType: TypeUsage) = targetType.isOfType(Triple::class.java)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<out Any>> {
        val firstType = targetType.getTypeArgument(0)
        val secondType = targetType.getTypeArgument(1)
        val thirdType = targetType.getTypeArgument(2)

        return subtypeProvider
            .resolveAndCombine(firstType, secondType, thirdType)
            .map { arbitraries ->
                val first = arbitraries[0]
                val second = arbitraries[1]
                val third = arbitraries[2]
                return@map anyTriple(first, second, third)
            }
            .collect(Collectors.toCollection(::LinkedHashSet))
    }
}