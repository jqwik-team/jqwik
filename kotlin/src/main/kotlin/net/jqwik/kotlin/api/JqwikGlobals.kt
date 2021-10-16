package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Functions
import org.apiguardian.api.API
import kotlin.reflect.KClass

/**
 * Function to create an arbitrary for [Pair<A, B>][Pair].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <A, B> anyPair(firstArbitrary: Arbitrary<A>, secondArbitrary: Arbitrary<B>) =
    combine(firstArbitrary, secondArbitrary) { a, b -> Pair(a, b) }

/**
 * Function to create an arbitrary for [Triple<A, B, C>][Triple].
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <A, B, C> anyTriple(firstArbitrary: Arbitrary<A>, secondArbitrary: Arbitrary<B>, thirdArbitrary: Arbitrary<C>) =
    combine(firstArbitrary, secondArbitrary, thirdArbitrary) { a, b, c -> Triple(a, b, c) }


/**
 * Function to create a [FunctionWrapper].
 *
 * This is a Kotlin convenience for [Functions.function] which requires a Java class instead.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction(kClass: KClass<*>): Functions.FunctionWrapper {
    return Functions.function(kClass.java)
}

/**
 * Create a [FunctionWrapper] for Kotlin function without parameters.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction0(): Functions.FunctionWrapper {
    return anyFunction(Function0::class)
}

/**
 * Create a [FunctionWrapper] for Kotlin function with 1 parameter.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction1(): Functions.FunctionWrapper {
    return anyFunction(Function1::class)
}

/**
 * Create a [FunctionWrapper] for Kotlin function with 2 parameters.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction2(): Functions.FunctionWrapper {
    return anyFunction(Function2::class)
}

/**
 * Create a [FunctionWrapper] for Kotlin function with 3 parameters.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction3(): Functions.FunctionWrapper {
    return anyFunction(Function3::class)
}

/**
 * Create a [FunctionWrapper] for Kotlin function with 4 parameters.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun anyFunction4(): Functions.FunctionWrapper {
    return anyFunction(Function4::class)
}
