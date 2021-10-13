package net.jqwik.kotlin.api

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*

class KotlinTypesExtensionsTests {

    @Example
    fun `String any()`(@ForAll random: Random) {
        val any = String.any().ofMaxLength(10)

        checkAllGenerated(
            any,
            random
        ) { value -> value is String && value.length <= 10 }
    }

    @Example
    fun `Char any()`(@ForAll random: Random) {
        val any = Char.any().numeric()

        checkAllGenerated(
            any,
            random
        ) { value -> value is Char && Character.isDigit(value) }
    }

    @Example
    fun `Char any(range)`(@ForAll random: Random) {
        val any = Char.any('0'..'9')

        checkAllGenerated(
            any,
            random
        ) { value -> value is Char && Character.isDigit(value) }
    }

    @Example
    fun `Boolean any()`(@ForAll random: Random) {
        val any = Boolean.any()

        checkAllGenerated(
            any,
            random
        ) { value -> value is Boolean }
    }


    @Example
    fun `Byte any()`(@ForAll random: Random) {
        val any = Byte.any().between(-100, 100)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Byte && value >= -100 && value <= 100 }
    }

    @Example
    fun `Byte any(range)`(@ForAll random: Random) {
        val any = Byte.any(-100..100)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Byte && value >= -100 && value <= 100 }
    }

    @Example
    fun `Short any()`(@ForAll random: Random) {
        val any = Short.any().between(42, 1000)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Short && value >= 42 && value <= 1000 }
    }

    @Example
    fun `Short any(range)`(@ForAll random: Random) {
        val any = Short.any(42..1000)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Short && value >= 42 && value <= 1000 }
    }

    @Example
    fun `Int any()`(@ForAll random: Random) {
        val any = Int.any().between(42, 1000)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Int && value >= 42 && value <= 1000 }
    }

    @Example
    fun `Int any(range)`(@ForAll random: Random) {
        val any = Int.any(42..1000)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Int && value >= 42 && value <= 1000 }
    }

    @Example
    fun `Long any()`(@ForAll random: Random) {
        val any = Long.any().between(42, 1000)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Long && value >= 42 && value <= 1000 }
    }

    @Example
    fun `Long any(range)`(@ForAll random: Random) {
        val any = Long.any(42L..10000L)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Long && value >= 42 && value <= 10000 }
    }

    @Example
    fun `Float any()`(@ForAll random: Random) {
        val any = Float.any().between(42.1f, 99.0f)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Float && value >= 42.1f && value <= 99.0f }
    }

    @Example
    fun `Float any(range)`(@ForAll random: Random) {
        val any = Float.any(42.1f..99.0f)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Float && value >= 42.1f && value <= 99.0f }
    }

    @Example
    fun `Double any()`(@ForAll random: Random) {
        val any = Double.any().between(42.1, 99.0)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Double && value >= 42.1 && value <= 99.0 }
    }

    @Example
    fun `Double any(range)`(@ForAll random: Random) {
        val any = Double.any(42.1..99.0)

        checkAllGenerated(
            any,
            random
        ) { value -> value is Double && value >= 42.1 && value <= 99.0 }
    }

}