package net.jqwik.kotlin.api

import net.jqwik.api.arbitraries.*
import org.apiguardian.api.API

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> ListArbitrary<T>.ofSize(range: IntRange): ListArbitrary<T> = ofMinSize(range.first).ofMaxSize(range.last)

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> SetArbitrary<T>.ofSize(range: IntRange): SetArbitrary<T> = ofMinSize(range.first).ofMaxSize(range.last)

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> StreamArbitrary<T>.ofSize(range: IntRange): StreamArbitrary<T> = ofMinSize(range.first).ofMaxSize(range.last)

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> IteratorArbitrary<T>.ofSize(range: IntRange): IteratorArbitrary<T> = ofMinSize(range.first).ofMaxSize(range.last)

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <K, V> MapArbitrary<K, V>.ofSize(range: IntRange): MapArbitrary<K, V> = ofMinSize(range.first).ofMaxSize(range.last)

@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T, A: Any> ArrayArbitrary<T, A>.ofSize(range: IntRange): ArrayArbitrary<T, A> = ofMinSize(range.first).ofMaxSize(range.last)
