package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.TypeArbitrary
import kotlin.reflect.KClass


/**
 * Creates [Arbitrary] with subtypes of a sealed class or interface [T].
 * If a subtype is a sealed class or interface, its subtypes are used to create [Arbitrary]. This is done recursively.
 * [TypeArbitrary] are created by default under the hood, but this can be customized, for each subtype, with [SubtypeScope.provide] .
 * ```kotlin
 * anyForSubtypeOf<MyInterface> {
 *     provide<MyImplementation1> { customArbitrary1() }
 *     provide<MyImplementation2> { customArbitrary2() }
 * }
 * ```
 * @param enableArbitraryRecursion is applied to all created [TypeArbitrary].
 */
inline fun <reified T> anyForSubtypeOf(
    enableArbitraryRecursion: Boolean = false,
    crossinline subtypeScope: SubtypeScope<T>.() -> Unit = {}
): Arbitrary<T> where T : Any {
    val scope = SubtypeScope<T>().apply(subtypeScope)
    return Arbitraries.of(T::class.allSealedSubclasses).flatMap {
        scope.getProviderFor(it)
            ?: Arbitraries.forType(it.java as Class<T>).run {
                if (enableArbitraryRecursion) {
                    enableRecursion()
                } else {
                    this
                }
            }
    }.map { obj -> obj as T }
}

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