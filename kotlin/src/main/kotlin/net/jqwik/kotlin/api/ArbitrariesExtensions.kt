package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.arbitraries.CharacterArbitrary
import net.jqwik.api.arbitraries.StringArbitrary
import org.apiguardian.api.API

/**
 * Convenience function to create an arbitrary for Strings.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun String.Companion.any() : StringArbitrary {
    return Arbitraries.strings()
}

/**
 * Convenience function to create an arbitrary for Chars.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun Char.Companion.any() : CharacterArbitrary {
    return Arbitraries.chars()
}