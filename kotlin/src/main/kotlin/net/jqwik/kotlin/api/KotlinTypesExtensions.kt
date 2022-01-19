package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.*
import org.apiguardian.api.API

/**
 * Convenience function to create an arbitrary for [String].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun String.Companion.any(): StringArbitrary {
    return Arbitraries.strings()
}

/**
 * Convenience function to create an arbitrary for [Char].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Char.Companion.any(): CharacterArbitrary {
    return Arbitraries.chars()
}

/**
 * Convenience function to create an arbitrary for [Char] in range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Char.Companion.any(range: CharRange): CharacterArbitrary {
    return Arbitraries.chars().range(range.first, range.last)
}

/**
 * Convenience function to create an arbitrary for [Boolean].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Boolean.Companion.any(): Arbitrary<Boolean> {
    return Arbitraries.of(false, true)
}

/**
 * Convenience function to create an arbitrary for [Byte].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Byte.Companion.any(): ByteArbitrary {
    return Arbitraries.bytes()
}

internal val MaxByteRage = (Byte.MIN_VALUE..Byte.MAX_VALUE)
/**
 * Convenience function to create an arbitrary for [Byte] in a range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Byte.Companion.any(range: IntRange): ByteArbitrary {
    validateRange(MaxByteRage, range)
    return Arbitraries.bytes().between(range.first.toByte(), range.last.toByte())
}

/**
 * Convenience function to create an arbitrary for [Short] in a range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Short.Companion.any(): ShortArbitrary {
    return Arbitraries.shorts()
}

internal val MaxShortRage = (Short.MIN_VALUE..Short.MAX_VALUE)
/**
 * Convenience function to create an arbitrary for [Short].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Short.Companion.any(range: IntRange): ShortArbitrary {
    validateRange(MaxShortRage, range)
    return Arbitraries.shorts().between(range.first.toShort(), range.last.toShort())
}

/**
 * Convenience function to create an arbitrary for [Int].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Int.Companion.any(): IntegerArbitrary {
    return Arbitraries.integers()
}

/**
 * Convenience function to create an arbitrary for [Int] in range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Int.Companion.any(range: IntRange): IntegerArbitrary {
    return Arbitraries.integers().between(range.first, range.last)
}

/**
 * Convenience function to create an arbitrary for [Long].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Long.Companion.any(): LongArbitrary {
    return Arbitraries.longs()
}

/**
 * Convenience function to create an arbitrary for [Long] in range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Long.Companion.any(range: LongRange): LongArbitrary {
    return Arbitraries.longs().between(range.first, range.last)
}

/**
 * Convenience function to create an arbitrary for [Float].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Float.Companion.any(): FloatArbitrary {
    return Arbitraries.floats()
}

/**
 * Convenience function to create an arbitrary for [Float] in range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Float.Companion.any(range: ClosedFloatingPointRange<Float>): FloatArbitrary {
    return Arbitraries.floats().between(range.start, range.endInclusive)
}

/**
 * Convenience function to create an arbitrary for [Double].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Double.Companion.any(): DoubleArbitrary {
    return Arbitraries.doubles()
}

/**
 * Convenience function to create an arbitrary for [Double] in range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Double.Companion.any(range: ClosedFloatingPointRange<Double>): DoubleArbitrary {
    return Arbitraries.doubles().between(range.start, range.endInclusive)
}

private fun validateRange(allowedRange: IntRange, range: IntRange) {
    if (!allowedRange.contains(range.first)) {
        val message = String.format("range.first [%s] must be in %s", range.first, allowedRange)
        throw IllegalArgumentException(message)
    }
    if (!allowedRange.contains(range.last)) {
        val message = String.format("range.last [%s] must be in %s", range.last, allowedRange)
        throw IllegalArgumentException(message)
    }
}

/**
 * Function to create an arbitrary for [IntRange].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun IntRange.Companion.any(): IntRangeArbitrary {
    return IntRangeArbitrary()
}

/**
 * Function to create an arbitrary for [IntRange] with range.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun IntRange.Companion.any(range: IntRange): IntRangeArbitrary {
    return IntRange.any().between(range.first, range.last)
}


/**
 * Function to create arbitrary for all values of an enum type.
 *
 * This is a Kotlin convenience for [Arbitraries.of] which requires the Java class of the enum instead.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
inline fun <reified T : Enum<T>> Enum.Companion.any(): Arbitrary<T> {
    return Arbitraries.of(T::class.java)
}

