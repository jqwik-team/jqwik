package net.jqwik.api

import org.apiguardian.api.API
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream

@API(status = API.Status.STABLE, since = "1.0")
fun interface RandomGenerator<out T> {
    @API(status = API.Status.INTERNAL)
    abstract class RandomGeneratorFacade {
        abstract fun <T, U> flatMap(
            self: Shrinkable<T>,
            mapper: Function<in T, out RandomGenerator<U>>,
            nextLong: Long
        ): Shrinkable<U>

        abstract fun <T, U> flatMap(
            wrappedShrinkable: Shrinkable<T>,
            mapper: Function<in T, out Arbitrary<U>>,
            genSize: Int,
            nextLong: Long,
            withEmbeddedEdgeCases: Boolean
        ): Shrinkable<U>

        abstract fun <T> filter(
            self: RandomGenerator<T>,
            filterPredicate: Predicate<in T>,
            maxMisses: Int
        ): RandomGenerator<T>

        abstract fun <T> withEdgeCases(
            self: RandomGenerator<T>,
            genSize: Int,
            edgeCases: EdgeCases<T>
        ): RandomGenerator<T>

        abstract fun <T> collect(self: RandomGenerator<T>, until: Predicate<in List<T>>): RandomGenerator<List<T>>
        abstract fun <T> injectDuplicates(self: RandomGenerator<T>, duplicateProbability: Double): RandomGenerator<T>
        abstract fun <T> ignoreException(
            self: RandomGenerator<T>,
            exceptionType: Class<out Throwable>
        ): RandomGenerator<T>

        companion object {
            val implementation = FacadeLoader.load(
                RandomGeneratorFacade::class.java
            )
        }
    }

    /**
     * @param random the source of randomness. Injected by jqwik itself.
     * @return the next generated value wrapped within the Shrinkable interface. The method must ALWAYS return a next value.
     */
    fun next(random: Random): Shrinkable<T>

    @API(status = API.Status.INTERNAL)
    fun <U> map(mapper: Function<in T, U>): RandomGenerator<U> {
        return mapShrinkable { s: Shrinkable<T> -> s.map(mapper) }
    }

    @API(status = API.Status.INTERNAL)
    fun <U> mapShrinkable(mapper: Function<in Shrinkable<T>, Shrinkable<U>>): RandomGenerator<U> {
        return RandomGenerator<U> { random: Random ->
            val tShrinkable = next(random)
            mapper.apply(tShrinkable)
        }
    }

    @API(status = API.Status.INTERNAL)
    fun <U> flatMap(mapper: Function<in T, RandomGenerator<U>>): RandomGenerator<U> {
        return RandomGenerator<U> { random: Random ->
            val wrappedShrinkable = next(random)
            RandomGeneratorFacade.implementation!!.flatMap(wrappedShrinkable, mapper, random.nextLong())
        }
    }

    @API(status = API.Status.INTERNAL)
    fun <U> flatMap(
        mapper: Function<in T, Arbitrary<U>>,
        genSize: Int,
        withEmbeddedEdgeCases: Boolean
    ): RandomGenerator<U> {
        return RandomGenerator<U> { random: Random ->
            val wrappedShrinkable = next(random)
            RandomGeneratorFacade.implementation
                .flatMap(wrappedShrinkable, mapper, genSize, random.nextLong(), withEmbeddedEdgeCases)
        }
    }

    @API(status = API.Status.INTERNAL)
    fun filter(filterPredicate: Predicate<in T>, maxMisses: Int): RandomGenerator<T>? {
        return RandomGeneratorFacade.implementation!!.filter(this, filterPredicate, maxMisses)
    }

    @API(status = API.Status.INTERNAL)
    fun withEdgeCases(genSize: Int, edgeCases: EdgeCases<@UnsafeVariance T>): RandomGenerator<T>? {
        return RandomGeneratorFacade.implementation!!.withEdgeCases(this, genSize, edgeCases)
    }

    @API(status = API.Status.INTERNAL)
    fun stream(random: Random): Stream<out Shrinkable<T>> {
        return Stream.generate { next(random) }
    }

    @API(status = API.Status.INTERNAL)
    fun collect(until: Predicate<in List<T>>): RandomGenerator<List<T>> {
        return RandomGeneratorFacade.implementation!!.collect(this, until)
    }

    @API(status = API.Status.INTERNAL)
    fun injectDuplicates(duplicateProbability: Double): RandomGenerator<T> {
        return RandomGeneratorFacade.implementation!!.injectDuplicates(this, duplicateProbability)
    }

    @API(status = API.Status.INTERNAL)
    fun ignoreException(exceptionType: Class<out Throwable>): RandomGenerator<T> {
        return RandomGeneratorFacade.implementation!!.ignoreException(this, exceptionType)
    }

    @API(status = API.Status.INTERNAL)
    fun dontShrink(): RandomGenerator<T> {
        return RandomGenerator<T> { random: Random ->
            val shrinkable = next(random).makeUnshrinkable()
            shrinkable.makeUnshrinkable()
        }
    }
}