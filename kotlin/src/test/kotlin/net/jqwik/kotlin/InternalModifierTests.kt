package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.Provide
import net.jqwik.api.lifecycle.AfterContainer
import net.jqwik.api.lifecycle.AfterProperty
import net.jqwik.kotlin.api.any

@PropertyDefaults(tries = 10)
internal class InternalModifierTests {

    companion object {
        var count: Int = 0

        @JvmStatic
        @AfterContainer
        // Will fail if tests are run in isolation
        fun threePropertiesRun() {
            assert(count == 5)
        }
    }

    @AfterProperty
    fun count() {
        count++
    }


    @Property
    fun publicPropertyInInternalClass() {
    }

    @Property
    fun `public property will have its name cut $ at dollar sign`() {
    }

    @Property
    internal fun internalPropertyInInternalClass() {
    }

    @Property
    internal fun internalPropertyWithSpecialParameter(@ForAll uInt: UInt) {
    }

    @Property
    fun internalProviderMethod(@ForAll("any") any: Any) {
        assert(any is String)
    }

    @Provide
    internal fun any() = String.any()
}

