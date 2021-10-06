package net.jqwik.kotlin.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.ArbitraryDecorator
import net.jqwik.kotlin.api.any

class UByteArbitrary : ArbitraryDecorator<UByte>() {

    private val base = Byte.any()

    override fun arbitrary(): Arbitrary<UByte> = base.map() { b -> b.toUByte() }

}