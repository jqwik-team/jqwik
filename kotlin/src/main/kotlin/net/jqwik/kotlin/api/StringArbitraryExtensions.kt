package net.jqwik.kotlin.api

import net.jqwik.api.arbitraries.StringArbitrary
import org.apiguardian.api.API

/**
 * Set the minimum and maximum allowed length of generated strings.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun StringArbitrary.ofLength(range: IntRange): StringArbitrary = ofMinLength(range.first).ofMaxLength(range.last)