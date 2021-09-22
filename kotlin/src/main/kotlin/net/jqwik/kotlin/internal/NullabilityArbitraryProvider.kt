package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.orNull
import kotlin.reflect.KParameter

class NullabilityArbitraryProvider : ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage): Boolean {
        val kParameter: KParameter? = targetType.parameterInfo
            .map { info -> kotlinParameter(info.get1(), info.get2()) }
            .orElse(null)
        return kParameter?.isMarkedNullable  ?: false
    }

    override fun provideFor(
        targetType: TypeUsage,
        subtypeProvider: ArbitraryProvider.SubtypeProvider
    ): MutableSet<Arbitrary<*>> {
        val nonNullType : TypeUsage = targetType.nonNull()
        val rawArbitraries = subtypeProvider.apply(nonNullType)
        return rawArbitraries.map { a -> a.orNull(0.05) }.toMutableSet()
    }

    override fun priority(): Int {
        // Replace most providers if type is nullable
        return 101
    }
}