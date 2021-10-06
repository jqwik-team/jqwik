package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.*
import net.jqwik.kotlin.api.arbitraries.UByteArbitrary
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

/**
 * Convenience function to create an arbitrary for [Short].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Short.Companion.any(): ShortArbitrary {
    return Arbitraries.shorts()
}

/**
 * Convenience function to create an arbitrary for [Int].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Int.Companion.any(): IntegerArbitrary {
    return Arbitraries.integers()
}

/**
 * Convenience function to create an arbitrary for [Long].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Long.Companion.any(): LongArbitrary {
    return Arbitraries.longs()
}

/**
 * Convenience function to create an arbitrary for [Float].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Float.Companion.any(): FloatArbitrary {
    return Arbitraries.floats()
}

/**
 * Convenience function to create an arbitrary for [Double].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Double.Companion.any(): DoubleArbitrary {
    return Arbitraries.doubles()
}

/**
 * Function to create an arbitrary for [UByte].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun UByte.Companion.any(): UByteArbitrary {
    return UByteArbitrary()
}

