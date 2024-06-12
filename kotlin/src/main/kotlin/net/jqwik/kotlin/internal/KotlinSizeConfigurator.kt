package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.configurators.ArbitraryConfiguratorBase
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.IntRangeArbitrary
import net.jqwik.kotlin.api.JqwikIntRange

class KotlinIntRangeConfigurator : ArbitraryConfiguratorBase() {
    override fun acceptTargetType(targetType: TypeUsage): Boolean {
        return targetType.isAssignableFrom(IntRange::class.java)
    }

    @Suppress("unused") // Used by jqwik
    fun configure(arbitrary: Arbitrary<IntRange>, range: JqwikIntRange): Arbitrary<IntRange> {
        return if (arbitrary is IntRangeArbitrary) {
            arbitrary.between(range.min, range.max)
        } else {
            arbitrary.filter { r: IntRange -> r.first >= range.min && r.last <= range.max }
        }
    }

}