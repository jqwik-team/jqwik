package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.sequence

class SequenceArbitraryProvider : ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage) = targetType.isOfType(Sequence::class.java)

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<out Any>> {
        val elementType = targetType.getTypeArgument(0)
        val elementArbitraries = subtypeProvider.apply(elementType)
        return elementArbitraries.map { it.sequence() }.toMutableSet()
    }
}