package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.arbitraries.StringArbitrary

fun String.Companion.any() : StringArbitrary {
    return Arbitraries.strings()
}