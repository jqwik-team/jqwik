package net.jqwik.api

import net.jqwik.api.arbitraries.*
import org.apiguardian.api.API
import java.util.stream.StreamSupport
import java.lang.AssertionError
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * The main interface for representing objects that can be generated and shrunk.
 *
 * @param <T> The type of generated objects. Primitive objects (e.g. int, boolean etc.) are represented by their boxed
 * type (e.g. Integer, Boolean).
</T> */
@API(status = API.Status.STABLE, since = "1.0")
interface Arbitrary<out T> {
    @API(status = API.Status.INTERNAL)
    abstract class ArbitraryFacade {
        abstract fun <T> list(elementArbitrary: Arbitrary<T>): ListArbitrary<T>
        abstract fun <T> set(elementArbitrary: Arbitrary<T>): SetArbitrary<T>
        abstract fun <T> stream(elementArbitrary: Arbitrary<T>): StreamArbitrary<T>
        abstract fun <T> iterator(elementArbitrary: Arbitrary<T>): IteratorArbitrary<T>
        abstract fun <T, A> array(elementArbitrary: Arbitrary<T>, arrayClass: Class<A>): ArrayArbitrary<T, A>
        abstract fun <T> sampleStream(arbitrary: Arbitrary<T>): Stream<T>
        abstract fun <T> injectNull(self: Arbitrary<T>, nullProbability: Double): Arbitrary<T>
        abstract fun <T> filter(self: Arbitrary<T>, filterPredicate: Predicate<in T>, maxMisses: Int): Arbitrary<T>
        abstract fun <T, U> map(self: Arbitrary<T>, mapper: Function<in T, out U>): Arbitrary<U>
        abstract fun <T, U> flatMap(self: Arbitrary<T>, mapper: Function<in T, out Arbitrary<U>>): Arbitrary<U>
        abstract fun <T> ignoreException(self: Arbitrary<T>, exceptionType: Class<out Throwable>): Arbitrary<T>
        abstract fun <T> dontShrink(self: Arbitrary<T>): Arbitrary<T>
        abstract fun <T> configureEdgeCases(
            self: Arbitrary<T>,
            configurator: Consumer<in EdgeCases.Config<T>>
        ): Arbitrary<T>

        abstract fun <T> withoutEdgeCases(self: Arbitrary<T>): Arbitrary<T>
        abstract fun <T> memoizedGenerator(
            self: Arbitrary<T>,
            genSize: Int,
            withEdgeCases: Boolean
        ): RandomGenerator<T>

        companion object {
            val implementation = FacadeLoader.load(
                ArbitraryFacade::class.java
            )
        }
    }

    /**
     * Create the random generator for an arbitrary.
     *
     *
     *
     * Starting with version 1.4.0 the returned generator should no longer
     * include edge cases explicitly since those will be injected in [.generator]
     *
     *
     * @param genSize a very unspecific configuration parameter that can be used
     * to influence the configuration and behaviour of a random generator
     * if and only if the generator wants to be influenced.
     * Many generators are independent of genSize.
     *
     *
     * The default value of `genSize` is the number of tries configured
     * for a property. Use [Arbitrary.fixGenSize] to fix
     * the parameter for a given arbitrary.
     * @return a new random generator instance
     */
    fun generator(genSize: Int): RandomGenerator<T>

    /**
     * Create the random generator for an arbitrary with or without edge cases.
     *
     *
     * Never override this method. Override [.generator] instead.
     *
     * @param genSize       See [.generator] about meaning of this parameter
     * @param withEdgeCases True if edge cases should be injected into the stream of generated values
     * @return a new random generator instance
     */
    @API(status = API.Status.INTERNAL, since = "1.4.0")
    fun generator(genSize: Int, withEdgeCases: Boolean): RandomGenerator<T> {
        return ArbitraryFacade.implementation!!.memoizedGenerator(this, genSize, withEdgeCases)
    }

    /**
     * Create the random generator for an arbitrary where the embedded generators,
     * if there are any, also generate edge cases.
     *
     *
     *
     * Override only if there are any embedded arbitraries / generators,
     * e.g. a container using an element generator
     *
     *
     * @param genSize See [.generator] about meaning of this parameter
     * @return a new random generator instance
     */
    @API(status = API.Status.INTERNAL, since = "1.4.0")
    fun generatorWithEmbeddedEdgeCases(genSize: Int): RandomGenerator<T> {
        return generator(genSize)
    }

    /**
     * @return The same instance but with type Arbitrary&lt;Object&gt;
     */
    @API(status = API.Status.INTERNAL)
    fun asGeneric(): Arbitrary<Any> {
        return this as Arbitrary<Any>
    }

    /**
     * Create the exhaustive generator for an arbitrary using the maximum allowed
     * number of generated samples. Just trying to find out if such a generator
     * exists might take a long time. This method should never be overridden.
     *
     * @return a new exhaustive generator or Optional.empty() if it cannot be created.
     */
    @API(status = API.Status.INTERNAL)
    fun exhaustive(): Optional<out ExhaustiveGenerator<T>> {
        return exhaustive(ExhaustiveGenerator.MAXIMUM_SAMPLES_TO_GENERATE)
    }

    /**
     * Create the exhaustive generator for an arbitrary. Depending on
     * `maxNumberOfSamples` this can take a long time.
     * This method must be overridden in all arbitraries that support exhaustive
     * generation.
     *
     * @param maxNumberOfSamples The maximum number of samples considered.
     * If during generation it becomes clear that this
     * number will be exceeded generation stops.
     * @return a new exhaustive generator or Optional.empty() if it cannot be created.
     */
    @API(status = API.Status.INTERNAL)
    fun exhaustive(maxNumberOfSamples: Long): Optional<out ExhaustiveGenerator<T>> {
        return Optional.empty()
    }

    fun edgeCases(maxEdgeCases: Int): EdgeCases<T>

    /**
     * Return an arbitrary's edge cases up to a limit of 1000.
     *
     *
     *
     * Never override. Override [.edgeCases] instead.
     *
     *
     * @return an instance of type [EdgeCases]
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.3.0")
    fun edgeCases(): EdgeCases<T> {
        return edgeCases(1000)
    }

    /**
     * Create optional stream of all possible values this arbitrary could generate.
     * This is only possible if the arbitrary is available for exhaustive generation.
     *
     * @return optional stream of all possible values
     */
    fun allValues(): Optional<out Stream<@UnsafeVariance T>> {
        return exhaustive().map { generator: ExhaustiveGenerator<T> ->
            StreamSupport.stream(
                generator.spliterator(),
                false
            )
        }
    }

    /**
     * Iterate through each value this arbitrary can generate if - and only if -
     * exhaustive generation is possible. This method can be used for example
     * to make assertions about a set of values described by an arbitrary.
     *
     * @param action the consumer function to be invoked for each value
     * @throws AssertionError if exhaustive generation is not possible
     */
    @API(status = API.Status.MAINTAINED, since = "1.1.2")
    fun forEachValue(action: Consumer<in T>) {
        if (!allValues().isPresent) throw AssertionError("Cannot generate all values of $this")
        allValues().ifPresent { stream: Stream<T> -> stream.forEach { t: T -> action.accept(t) } }
    }

    /**
     * Create a new arbitrary of the same type `T` that creates and shrinks the original arbitrary but only allows
     * values that are accepted by the `filterPredicate`.
     *
     * @param filterPredicate The predicate used for filtering
     * @return a new arbitrary instance
     * @throws TooManyFilterMissesException if filtering will fail to come up with a value after 10000 tries
     */
    fun filter(filterPredicate: Predicate<in T>): Arbitrary<T> {
        return filter(filterPredicate, 10000)
    }

    /**
     * Create a new arbitrary of the same type `T` that creates and shrinks the original arbitrary but only allows
     * values that are accepted by the `filterPredicate`.
     *
     * @param filterPredicate The predicate used for filtering
     * @param maxMisses The max number of misses allowed for filtering
     * @return a new arbitrary instance
     * @throws TooManyFilterMissesException if filtering will fail to come up with a value after `maxMisses` tries
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.6.1")
    fun filter(filterPredicate: Predicate<in T>, maxMisses: Int): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.filter(this, filterPredicate, maxMisses)
    }

    /**
     * Create a new arbitrary of type `U` that maps the values of the original arbitrary using the `mapper`
     * function.
     *
     * @param <U>    type of resulting object
     * @param mapper the function used to map
     * @return a new arbitrary instance
    </U> */
    fun <U> map(mapper: Function<T, U>): Arbitrary<U> {
        return ArbitraryFacade.implementation!!.map(this, mapper)
    }

    /**
     * Create a new arbitrary of type `U` that uses the values of the existing arbitrary to create a new arbitrary
     * using the `mapper` function.
     *
     * @param <U>    type of resulting object
     * @param mapper the function used to map to arbitrary
     * @return a new arbitrary instance
    </U> */
    fun <U> flatMap(mapper: Function<in T, out Arbitrary<U>>): Arbitrary<U> {
        return ArbitraryFacade.implementation!!.flatMap(this, mapper)
    }

    /**
     * Create a new arbitrary of the same type but inject null values with a probability of `nullProbability`.
     *
     * @param nullProbability the probability.  0 and  1.
     * @return a new arbitrary instance
     */
    fun injectNull(nullProbability: Double): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.injectNull(this@Arbitrary, nullProbability)
    }

    /**
     * Fix the genSize of an arbitrary so that it can no longer be influenced from outside
     *
     * @param genSize The size used in arbitrary instead of the dynamic one
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.2.0")
    fun fixGenSize(genSize: Int): Arbitrary<T> {
        return object : Arbitrary<T> {
            override fun generator(ignoredGenSize: Int): RandomGenerator<T> {
                return this@Arbitrary.generator(genSize)
            }

            override fun exhaustive(maxNumberOfSamples: Long): Optional<out ExhaustiveGenerator<T>> {
                return this@Arbitrary.exhaustive(maxNumberOfSamples)
            }

            override fun edgeCases(maxEdgeCases: Int): EdgeCases<T> {
                return this@Arbitrary.edgeCases(maxEdgeCases)
            }
        }
    }

    /**
     * Create a new arbitrary of type `List<T>` using the existing arbitrary for generating the elements of the list.
     *
     * @return a new arbitrary instance
     */
    fun list(): ListArbitrary<T> {
        return ArbitraryFacade.implementation!!.list(this)
    }

    /**
     * Create a new arbitrary of type `Set<T>` using the existing arbitrary for generating the elements of the set.
     *
     * @return a new arbitrary instance
     */
    fun set(): SetArbitrary<T> {
        return ArbitraryFacade.implementation!!.set(this)
    }

    /**
     * Create a new arbitrary of type `Stream<T>` using the existing arbitrary
     * for generating the elements of the stream.
     *
     * @return a new arbitrary instance
     */
    fun stream(): StreamArbitrary<T> {
        return ArbitraryFacade.implementation!!.stream(this)
    }

    /**
     * Create a new arbitrary of type `Iterable<T>` using the existing arbitrary for generating the elements of the
     * stream.
     *
     * @return a new arbitrary instance
     */
    operator fun iterator(): IteratorArbitrary<T> {
        return ArbitraryFacade.implementation!!.iterator(this)
    }

    /**
     * Create a new arbitrary of type `T[]` using the existing arbitrary for generating the elements of the array.
     *
     * @param <A>        Type of resulting array class
     * @param arrayClass The arrays class to create, e.g. `String[].class`. This is required due to limitations in Java's
     * reflection capabilities.
     * @return a new arbitrary instance
    </A> */
    fun <A> array(arrayClass: Class<A>): ArrayArbitrary<T, A> {
        return ArbitraryFacade.implementation!!.array(this, arrayClass)
    }

    /**
     * Create a new arbitrary of type `Optional<T>` using the existing arbitrary for generating the elements of the
     * stream.
     *
     *
     *
     * The new arbitrary generates `Optional.empty()` values with a probability of `0.05` (i.e. 1 in 20).
     *
     *
     * @return a new arbitrary instance
     */
    fun optional(): Arbitrary<Optional<out T>> {
        return optional(0.95)
    }

    /**
     * Create a new arbitrary of type `Optional<T>` using the existing arbitrary for generating the elements of the
     * stream.
     *
     *
     *
     * The new arbitrary generates `Optional.empty()` values with a probability of `1 - presenceProbability`.
     *
     *
     * @param presenceProbability The probability with which a value is present, i.e. not empty
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.5.4")
    fun optional(presenceProbability: Double): Arbitrary<Optional<out T>> {
        val emptyProbability = 1.0 - presenceProbability
        return injectNull(emptyProbability).map { value: T -> Optional.ofNullable(value) }
    }

    /**
     * Create a new arbitrary of type `List<T>` by adding elements of type T until condition `until` is fulfilled.
     *
     * @param until predicate to check if final condition has been reached
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun collect(until: Predicate<in List<T>>): Arbitrary<List<T>> {
        return object : Arbitrary<List<T>> {
            override fun generator(genSize: Int): RandomGenerator<List<T>> {
                return this@Arbitrary.generator(genSize).collect(until)
            }

            override fun edgeCases(maxEdgeCases: Int): EdgeCases<List<T>> {
                return EdgeCases.none()
            }
        }
    }

    /**
     * Generate a stream of sample values using this arbitrary.
     * This can be useful for
     *
     *  * Testing arbitraries
     *  * Playing around with arbitraries in *jshell*
     *  * Using arbitraries independently from jqwik, e.g. to feed test data builders
     *
     *
     *
     *
     * The underlying generator is created with size 1000.
     * Outside a property a new instance of [Random] will be created
     * to feed the generator.
     *
     *
     *
     *
     * Using this method within a property does not break reproducibility of results,
     * i.e. rerunning it with same seed will also generate the same values.
     *
     *
     * @return a stream of newly generated values
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun sampleStream(): Stream<out T> {
        return ArbitraryFacade.implementation!!.sampleStream(this)
    }

    /**
     * Generate a single sample value using this arbitrary.
     * This can be useful for
     *
     *  * Testing arbitraries
     *  * Playing around with arbitraries in *jshell*
     *  * Using arbitraries independently from jqwik, e.g. to feed test data builders
     *
     *
     *
     * Some additional things to be aware of:
     *
     *  *
     * If you feel the need to use this method for real generation, e.g. in a provider method
     * you are most probably doing it wrong. You might want to use [Arbitrary.flatMap].
     *
     *  *
     * The underlying generator is created with size 1000.
     * Outside a property a new instance of [Random] will be created
     * to feed the generator.
     *  *
     * Using this method within a property does not break reproducibility of results,
     * i.e. rerunning it with same seed will also generate the same value.
     *
     *
     *
     * @return a newly generated value
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun sample(): T {
        return sampleStream()
            .map<Optional<T>> { value: T -> Optional.ofNullable(value) }
            .findFirst()
            .orElseThrow { JqwikException("Cannot generate a value") }
            .orElse(null)
    }

    /**
     * Create a new arbitrary of type `Iterable<T>` that will
     * inject duplicates of previously generated values with a probability of `duplicateProbability`.
     *
     *
     *
     * Shrinking behavior for duplicate values
     * -- if duplication is required for falsification -- is poor,
     * i.e. those duplicate values cannot be shrunk to "smaller" duplicate values.
     *
     *
     * @param duplicateProbability The probability with which a previous value will be generated
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun injectDuplicates(duplicateProbability: Double): Arbitrary<T> {
        return object : Arbitrary<T> {
            override fun generator(genSize: Int): RandomGenerator<T> {
                return this@Arbitrary.generator(genSize).injectDuplicates(duplicateProbability)
            }

            override fun generatorWithEmbeddedEdgeCases(genSize: Int): RandomGenerator<T> {
                return this@Arbitrary.generatorWithEmbeddedEdgeCases(genSize).injectDuplicates(duplicateProbability)
            }

            override fun exhaustive(maxNumberOfSamples: Long): Optional<out ExhaustiveGenerator<T>> {
                return this@Arbitrary.exhaustive(maxNumberOfSamples)
            }

            override fun edgeCases(maxEdgeCases: Int): EdgeCases<T> {
                return if (duplicateProbability >= 1.0) {
                    // This is a pathological case anyway
                    EdgeCases.none()
                } else this@Arbitrary.edgeCases(maxEdgeCases)
            }
        }
    }

    /**
     * Create a new arbitrary of type `Tuple.Tuple1<T>` that will use the underlying
     * arbitrary to create the tuple value;
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun tuple1(): Arbitrary<Tuple.Tuple1<out T>> {
        return this@Arbitrary.map { v1: T -> Tuple.of(v1) }
    }

    /**
     * Create a new arbitrary of type `Tuple.Tuple2<T, T>` that will use the underlying
     * arbitrary to create the tuple values;
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun tuple2(): Arbitrary<Tuple.Tuple2<out T, out T>> {
        return list().ofSize(2).map { l: List<T> ->
            Tuple.of(
                l[0], l[1]
            )
        }
    }

    /**
     * Create a new arbitrary of type `Tuple.Tuple3<T, T, T>` that will use the underlying
     * arbitrary to create the tuple values;
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun tuple3(): Arbitrary<Tuple.Tuple3<out T, out T, out T>> {
        return list().ofSize(3).map { l: List<T> ->
            Tuple.of(
                l[0], l[1], l[2]
            )
        }
    }

    /**
     * Create a new arbitrary of type `Tuple.Tuple4<T, T, T, T>` that will use the underlying
     * arbitrary to create the tuple values;
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.0")
    fun tuple4(): Arbitrary<Tuple.Tuple4<out T, out T, out T, out T>> {
        return list().ofSize(4).map { l: List<T> ->
            Tuple.of(
                l[0], l[1], l[2], l[3]
            )
        }
    }

    /**
     * Create a new arbitrary of type `Tuple.Tuple5<T, T, T, T, T>` that will use the underlying
     * arbitrary to create the tuple values;
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.3")
    fun tuple5(): Arbitrary<Tuple.Tuple5<out T, out T, out T, out T, out T>> {
        return list().ofSize(5).map { l: List<T> ->
            Tuple.of(
                l[0], l[1], l[2], l[3], l[4]
            )
        }
    }

    /**
     * Create a new arbitrary of type `T` that will use the underlying
     * arbitrary to create the tuple values but will ignore any raised exception of
     * type `exceptionType` during generation.
     *
     * @param exceptionType The exception type to ignore
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.3.1")
    fun ignoreException(exceptionType: Class<out Throwable>): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.ignoreException(this@Arbitrary, exceptionType)
    }

    /**
     * Create a new arbitrary of type `T` that will use the underlying
     * arbitrary to create the tuple values but will return unshrinkable values.
     * This might be necessary if values are being mutated during a property run
     * and the mutated state would make a shrunk value invalid.
     *
     *
     *
     * This is a hack to get around a weakness in jqwik's shrinking mechanism
     *
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun dontShrink(): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.dontShrink(this@Arbitrary)
    }

    /**
     * Experimental interface to change generated edge cases of a specific arbitrary.
     *
     * @param configurator A consumer that configures deviating edge cases behaviour
     * @return a new arbitrary instance
     * @see EdgeCases.Config
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.3.9")
    fun edgeCases(configurator: Consumer<in EdgeCases.Config<@UnsafeVariance T>>): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.configureEdgeCases(this@Arbitrary, configurator)
    }

    /**
     * Create a new arbitrary of type `T` that will not explicitly generate
     * any edge cases, neither directly or in embedded arbitraries.
     * This is useful if you want to prune selected branches of edge case generation
     * because they are to costly or generate too many cases.
     *
     * @return a new arbitrary instance
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.4.0")
    fun withoutEdgeCases(): Arbitrary<T> {
        return ArbitraryFacade.implementation!!.withoutEdgeCases(this@Arbitrary)
    }
}