package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import kotlin.reflect.KClass

/**
 * All sealed subclasses, recursively.
 */
val <T : Any> KClass<T>.allSealedSubclasses: List<KClass<out T>>
    get() = sealedSubclasses.flatMap {
        if (it.isSealed) {
            it.allSealedSubclasses
        } else {
            listOf(it)
        }
    }

class SubtypeScope<T> {
    val customProviders = mutableListOf<CustomProvider<T>>()

    /**
     * Registers a custom provider for subtype [U], instead of default one created by [anyForSubtypeOf].
     */
    inline fun <reified U> provide(noinline customProvider: () -> Arbitrary<U>) where U : T {
        customProviders.add(CustomProvider(U::class as KClass<Any>, customProvider) as CustomProvider<T>)
    }

    /**
     * @return custom provider registered with [provide], or null.
     */
    fun getProviderFor(targetType: KClass<*>) =
        customProviders.firstOrNull { it.targetType == targetType }?.arbitraryFactory?.invoke()

    class CustomProvider<T>(
        val targetType: KClass<Any>,
        val arbitraryFactory: () -> Arbitrary<T>
    )
}