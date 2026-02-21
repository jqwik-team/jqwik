package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import org.apiguardian.api.API
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

@API(status = API.Status.EXPERIMENTAL, since = "1.8.4")
class SubtypeScope<T: Any> {
    val customProviders = mutableListOf<CustomProvider<T>>()

    /**
     * Registers a custom provider for subtype [U], instead of default one created by [anyForSubtypeOf].
     */
    inline fun <reified U> provide(noinline customProvider: () -> Arbitrary<U>) where U : T {
        customProviders.add(CustomProvider(U::class, customProvider))
    }

    /**
     * @return custom provider registered with [provide], or null.
     */
    fun getProviderFor(targetType: KClass<*>) =
        customProviders.firstOrNull { it.targetType == targetType }?.arbitraryFactory?.invoke()

    class CustomProvider<out T: Any>(
        val targetType: KClass<out T>,
        val arbitraryFactory: () -> Arbitrary<out T>
    )

    override fun hashCode(): Int {
        return customProviders.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubtypeScope<*>

        return customProviders == other.customProviders
    }
}