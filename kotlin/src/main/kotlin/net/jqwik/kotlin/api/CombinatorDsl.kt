package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Combinators
import net.jqwik.api.Combinators.ListCombinator
import org.apiguardian.api.API
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Combine arbitraries using the combinator DSL:
 *
 * ```kotlin
 * combine {
 *     val first by Arbitraries.strings()
 *     val second by Arbitraries.strings()
 *
 *     filter { first.isNotEmpty() }
 *     filter { first != second }
 *
 *     combineAs {
 *         "first: $first, second: $second"
 *     }
 * }
 * ```
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
fun <R> combine(
    combinator: CombinatorScope.() -> Combined<R>
): Arbitrary<R> {
    val bindings = ValueBindings()
    val combined = CombinatorScope(bindings).combinator()

    return combined.createArbitrary(bindings)
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class ArbitraryProperty<T> internal constructor(
    private val bindings: ValueBindings,
    private val index: Int,
) : ReadOnlyProperty<Nothing?, T> {
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): T {
        return bindings[index]
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class CombinatorScope internal constructor(private val bindings: ValueBindings) {
    private val created = AtomicBoolean(false)
    private val arbitraries = mutableListOf<Arbitrary<*>>()
    private val filters = mutableListOf<() -> Boolean>()

    operator fun <T> Arbitrary<T>.provideDelegate(thisRef: Nothing?, property: KProperty<*>): ArbitraryProperty<T> {
        arbitraries += this

        return ArbitraryProperty(bindings, arbitraries.lastIndex)
    }

    fun filter(filter: () -> Boolean) {
        filters += filter
    }

    fun <R> combineAs(creator: () -> R): Combined<R> {
        check(!created.getAndSet(true)) { "'combineAs' must only be called once" }

        return Combined.Regular(arbitraries.toList(), filters.toList(), creator)
    }

    fun <R> flatCombineAs(creator: () -> Arbitrary<R>): Combined<R> {
        check(!created.getAndSet(true)) { "'flatCombineAs' must only be called once" }

        return Combined.Flat(arbitraries.toList(), filters.toList(), creator)
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
sealed class Combined<R>(
    private val arbitraries: List<Arbitrary<*>>,
    private val filters: List<() -> Boolean>
) {
    internal fun createArbitrary(bindings: ValueBindings): Arbitrary<R> {
        @Suppress("UNCHECKED_CAST")
        var combinator: ListCombinator<*> = Combinators.combine(arbitraries as List<Arbitrary<Any?>>)

        if (filters.isNotEmpty()) {
            combinator = combinator.filter { values ->
                bindings.withValues(values) {
                    filters.all { it() }
                }
            }
        }

        return combinator.asArbitrary(bindings)
    }

    internal abstract fun ListCombinator<*>.asArbitrary(bindings: ValueBindings): Arbitrary<R>

    internal class Regular<R>(
        arbitraries: List<Arbitrary<*>>,
        filters: List<() -> Boolean>,
        val creator: () -> R
    ) : Combined<R>(arbitraries, filters) {
        override fun ListCombinator<*>.asArbitrary(bindings: ValueBindings): Arbitrary<R> {
            return `as` { values ->
                bindings.withValues(values) {
                    creator()
                }
            }
        }
    }

    internal class Flat<R>(
        arbitraries: List<Arbitrary<*>>,
        filters: List<() -> Boolean>,
        val creator: () -> Arbitrary<R>
    ) : Combined<R>(arbitraries, filters) {
        override fun ListCombinator<*>.asArbitrary(bindings: ValueBindings): Arbitrary<R> {
            return flatAs { values ->
                bindings.withValues(values) {
                    creator()
                }
            }
        }
    }
}

/**
 * A class that provides the [ArbitraryProperty] instances access to the relevant values when accessed from within the
 * block passed to [CombinatorScope.combineAs].
 */
internal class ValueBindings {
    // Because the ArbitraryProperty instances have to be created once per "combine" call (so once per Arbitrary
    // instance), they are shared across all invocations of the "combineAs" block. To prevent issues with potential
    // parallel generation of arbitrary values, we use a ThreadLocal here.
    private val current = ThreadLocal<List<*>>()

    operator fun <T> get(index: Int): T {
        val current = checkNotNull(current.get()) {
            "Arbitrary delegate property must only be used inside 'combineAs', 'flatCombineAs' or 'filter'"
        }

        @Suppress("UNCHECKED_CAST")
        return current[index] as T
    }

    fun <R> withValues(values: List<*>, block: () -> R): R {
        return try {
            current.set(values)

            block()
        } finally {
            current.set(null)
        }
    }
}
