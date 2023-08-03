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
 *     createAs {
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
    val listCombinator = combined.createListCombinator(bindings)

    return listCombinator.`as` { values ->
        bindings.withValues(values) {
            combined.creator()
        }
    } as Arbitrary<R>
}

/**
 * Flat-combine arbitraries using the combinator DSL:
 *
 * ```kotlin
 * flatCombine {
 *     val first by Arbitraries.strings()
 *     val second by Arbitraries.strings()
 *
 *     filter { first.isNotEmpty() }
 *     filter { first != second }
 *
 *     createAs {
 *         Arbitraries.just("first: $first, second: $second")
 *     }
 * }
 * ```
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
fun <R> flatCombine(
    combinator: CombinatorScope.() -> Combined<Arbitrary<R>>
): Arbitrary<R> {
    val bindings = ValueBindings()
    val combined = CombinatorScope(bindings).combinator()
    val listCombinator = combined.createListCombinator(bindings)

    return listCombinator.flatAs { values ->
        bindings.withValues(values) {
            combined.creator()
        }
    } as Arbitrary<R>
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
    private var created = AtomicBoolean(false)
    private val arbitraries = mutableListOf<Arbitrary<*>>()
    private val filters = mutableListOf<() -> Boolean>()

    operator fun <T> Arbitrary<T>.provideDelegate(thisRef: Nothing?, property: KProperty<*>): ArbitraryProperty<T> {
        arbitraries += this

        return ArbitraryProperty(bindings, arbitraries.lastIndex)
    }

    fun filter(filter: () -> Boolean) {
        filters += filter
    }

    fun <R> createAs(creator: () -> R): Combined<R> {
        check(!created.getAndSet(true)) { "'createAs' must only be called once" }

        return Combined(arbitraries.toList(), filters.toList(), creator)
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class Combined<R> internal constructor(
    val arbitraries: List<Arbitrary<*>>,
    val filters: List<() -> Boolean>,
    val creator: () -> R
) {
    internal fun createListCombinator(bindings: ValueBindings): ListCombinator<*> {
        @Suppress("UNCHECKED_CAST")
        var combinator: ListCombinator<*> = Combinators.combine(arbitraries as List<Arbitrary<Any?>>)

        if (filters.isNotEmpty()) {
            combinator = combinator.filter { values ->
                bindings.withValues(values) {
                    filters.all { it() }
                }
            }
        }

        return combinator
    }
}

/**
 * A class that provides the [ArbitraryProperty] instances access to the relevant values when accessed from within the
 * block passed to [CombinatorScope.createAs].
 */
internal class ValueBindings {
    // Because the ArbitraryProperty instances have to be created once per "combine" call (so once per Arbitrary
    // instance), they are shared across all invocations of the "createAs" block. To prevent issues with potential
    // parallel generation of arbitrary values, we use a ThreadLocal here.
    private val current = ThreadLocal<List<*>>()

    operator fun <T> get(index: Int): T {
        val current = checkNotNull(current.get()) {
            "Arbitrary delegate property must only be used inside 'combineAs' or 'filter'"
        }

        @Suppress("UNCHECKED_CAST")
        return current[index] as T
    }

    fun <R> withValues(values: List<*>, block: () -> R): R {
        return try {
            current.set(values)

            block()
        } finally {
            current.remove()
        }
    }
}
