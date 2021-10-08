package net.jqwik.kotlin.internal

import net.jqwik.api.providers.ArbitraryProvider
import net.jqwik.api.providers.ArbitraryProvider.SubtypeProvider
import net.jqwik.api.Arbitrary
import net.jqwik.api.Arbitraries
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.kotlinType
import kotlin.reflect.full.createType

class UByteArbitraryProvider : ArbitraryProvider {
    override fun canProvideFor(targetType: TypeUsage): Boolean {
        return targetType.kotlinType?.equals(UByte::class.createType()) ?: false
    }

    override fun provideFor(targetType: TypeUsage, subtypeProvider: SubtypeProvider): Set<Arbitrary<*>> {
        //return setOf<Arbitrary<*>>(Arbitraries.just(199.toUByte().toByte()))
        return setOf<Arbitrary<*>>(UByte.any())
    }

    override fun priority() = 1
}