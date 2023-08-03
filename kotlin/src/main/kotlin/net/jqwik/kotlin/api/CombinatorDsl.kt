package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import org.apiguardian.api.API
import java.util.Optional
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
    val combined = CombinatorScope().combinator()

    @Suppress("UNCHECKED_CAST")
    return combine(combined.arbitraries as List<Arbitrary<Any?>>) { values ->
        try {
            for ((property, value) in combined.properties zip values) {
                property.bind(value)
            }

            CombinatorCreateScope().(combined.combinator)()
        } finally {
            for (it in combined.properties) {
                it.unbind()
            }
        }
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class ArbitraryProperty<T> internal constructor() : ReadOnlyProperty<Nothing?, T> {
    private val currentValue = ThreadLocal<Optional<T & Any>>()

    internal fun bind(value: Any?) {
        @Suppress("UNCHECKED_CAST")
        currentValue.set(Optional.ofNullable(value as T))
    }

    internal fun unbind() {
        currentValue.remove()
    }

    override fun getValue(thisRef: Nothing?, property: KProperty<*>): T {
        val values = currentValue.get() ?: run {
            error("Arbitrary delegate must only be used inside 'combineAs'")
        }

        return values.orElse(null)
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class CombinatorScope internal constructor() {
    private val arbitraries = mutableListOf<Arbitrary<*>>()
    private val properties = mutableListOf<ArbitraryProperty<*>>()

    operator fun <T> Arbitrary<T>.provideDelegate(thisRef: Nothing?, property: KProperty<*>): ArbitraryProperty<T> {
        val arbitraryProperty = ArbitraryProperty<T>()

        arbitraries += this
        properties += arbitraryProperty

        return arbitraryProperty
    }

    fun <R> createAs(creator: CombinatorCreateScope.() -> R): Combined<R> {
        return Combined(arbitraries.toList(), properties.toList(), creator)
    }
}

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class CombinatorCreateScope internal constructor()

@API(status = API.Status.EXPERIMENTAL, since = "1.8.0")
class Combined<R> internal constructor(
    val arbitraries: List<Arbitrary<*>>,
    val properties: List<ArbitraryProperty<*>>,
    val combinator: CombinatorCreateScope.() -> R
)
