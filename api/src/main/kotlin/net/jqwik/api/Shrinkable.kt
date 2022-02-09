package net.jqwik.api

import org.apiguardian.api.API
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream

@API(status = API.Status.STABLE, since = "1.0")
interface Shrinkable<out T> : Comparable<Shrinkable<@UnsafeVariance T>> {
    @API(status = API.Status.INTERNAL)
    abstract class ShrinkableFacade {
        abstract fun <T> unshrinkable(valueSupplier: Supplier<T>, distance: ShrinkingDistance): Shrinkable<T>
        abstract fun <T, U> map(self: Shrinkable<T>, mapper: Function<in T, out U>): Shrinkable<U>
        abstract fun <T> filter(self: Shrinkable<T>, filter: Predicate<in T>): Shrinkable<T>
        abstract fun <T, U> flatMap(
            self: Shrinkable<T>,
            flatMapper: Function<in T, Arbitrary<U>?>?,
            tries: Int,
            randomSeed: Long
        ): Shrinkable<U>?

        companion object {
            val implementation = FacadeLoader.load(
                ShrinkableFacade::class.java
            )
        }
    }

    /**
     * Create value freshly, so that in case of mutable objects shrinking (and reporting)
     * can rely on untouched values.
     *
     * @return An un-changed instance of the value represented by this shrinkable
     */
    fun value(): T

    /**
     * Create a new and finite stream of smaller or same size shrinkables; size is measured by [.distance].
     *
     *
     * Same size shrinkables are allowed but they have to iterate towards a single value to prevent endless shrinking.
     * This also means that a shrinkable must never be in its own shrink stream!
     *
     * @return a finite stream of shrinking options
     */
    @API(status = API.Status.INTERNAL, since = "1.3.3")
    fun shrink(): Stream<out Shrinkable<T>>

    /**
     * To be able to "move" values towards the end of collections while keeping some constraint constant
     * it's necessary to grow a shrinkable by what another has been shrunk.
     * One example is keeping a sum of values and still shrinking to the same resulting list.
     *
     * @param before The other shrinkable before shrinking
     * @param after The other shrinkable after shrinking
     * @return this shrinkable grown by the difference of before and after
     */
    @API(status = API.Status.INTERNAL, since = "1.3.3")
    fun grow(before: Shrinkable<*>?, after: Shrinkable<*>?): Optional<out Shrinkable<T>> {
        return Optional.empty()
    }

    /**
     * Grow a shrinkable to allow broader searching in flat mapped shrinkables
     *
     * @return a finite stream of grown values
     */
    @API(status = API.Status.INTERNAL, since = "1.3.3")
    fun grow(): Stream<out Shrinkable<T>>? {
        return Stream.empty()
    }

    fun distance(): ShrinkingDistance

    /**
     * Sometimes simplifies test writing
     *
     * @return generic version of a shrinkable
     */
    @API(status = API.Status.INTERNAL, since = "1.2.4")
    fun asGeneric(): Shrinkable<Any?>? {
        return this as Shrinkable<Any?>
    }

    fun <U> map(mapper: Function<in T, U>): Shrinkable<U> {
        return ShrinkableFacade.implementation!!.map(this, mapper)
    }

    fun filter(filter: Predicate<in T>): Shrinkable<T> {
        return ShrinkableFacade.implementation!!.filter(this, filter)
    }

    fun <U> flatMap(flatMapper: Function<in T, Arbitrary<U>?>?, tries: Int, randomSeed: Long): Shrinkable<U>? {
        return ShrinkableFacade.implementation!!.flatMap(this, flatMapper, tries, randomSeed)
    }

    @API(status = API.Status.INTERNAL)
    override fun compareTo(other: Shrinkable<@UnsafeVariance T>): Int {
        val comparison = distance().compareTo(other.distance())
        if (comparison == 0) {
            val value = value()
            if (value is Comparable<*> && this.javaClass == other.javaClass) {
                return (value as Comparable<T>).compareTo(other.value())
            }
        }
        return comparison
    }

    @API(status = API.Status.INTERNAL)
    fun makeUnshrinkable(): Shrinkable<T> {
        return ShrinkableFacade.implementation!!.unshrinkable({ value() }, distance())
    }

    companion object {
        @JvmStatic
        fun <T> unshrinkable(value: T): Shrinkable<T> {
            return unshrinkable<T>(value, ShrinkingDistance.of(0))
        }

        @JvmStatic
		fun <T> unshrinkable(value: T, distance: ShrinkingDistance): Shrinkable<T> {
            return ShrinkableFacade.implementation!!.unshrinkable({ value }, distance)
        }

        @JvmStatic
		@API(status = API.Status.INTERNAL)
        fun <T> supplyUnshrinkable(supplier: Supplier<T>, cacheImmutables: Boolean): Shrinkable<T> {
            return ShrinkableFacade.implementation!!.unshrinkable(supplier, ShrinkingDistance.of(0))
        }
    }
}