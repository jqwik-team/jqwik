package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import net.jqwik.api.arbitraries.ArbitraryDecorator
import net.jqwik.api.arbitraries.ListArbitrary
import net.jqwik.api.arbitraries.SizableArbitrary
import org.apiguardian.api.API
import org.apiguardian.api.API.Status.EXPERIMENTAL
import org.jspecify.annotations.Nullable
import java.util.function.Function

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type [Sequence]
 */
@API(status = EXPERIMENTAL, since = "1.6.0")
class SequenceArbitrary<T>(elementArbitrary: Arbitrary<T>) : ArbitraryDecorator<Sequence<T>>(),
    SizableArbitrary<Sequence<T>> {

    private var listArbitrary: ListArbitrary<T>

    init {
        this.listArbitrary = elementArbitrary.list()
    }

    override fun arbitrary(): Arbitrary<Sequence<T>> {
        return listArbitrary.map { m -> m.asSequence() }
    }

    override fun ofSize(size: Int): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.ofSize(size)
        return clone
    }

    fun ofSize(range: IntRange): SequenceArbitrary<T> {
        return ofMinSize(range.first).ofMaxSize(range.last)
    }

    override fun ofMinSize(minSize: Int): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.ofMinSize(minSize)
        return clone
    }

    override fun ofMaxSize(maxSize: Int): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.ofMaxSize(maxSize)
        return clone
    }

    override fun withSizeDistribution(distribution: RandomDistribution): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.withSizeDistribution(distribution)
        return clone
    }

    /**
     * Add the constraint that elements of the generated iterator must be unique,
     * i.e. no two elements must return true when being compared using [Object.equals].
     *
     * The constraint can be combined with other [.uniqueElements] constraints.
     *
     * @return new arbitrary instance
     */
    fun uniqueElements(): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.uniqueElements()
        return clone
    }

    /**
     * Add the constraint that elements of the generated iterator must be unique
     * relating to an element's "feature" being extracted using the
     * `by` function.
     * The extracted features are being compared using [Object.equals].
     *
     * The constraint can be combined with other [.uniqueElements] constraints.
     *
     * @return new arbitrary instance
     */
    fun uniqueElements(by: Function<in T, *>): SequenceArbitrary<T> {
        val clone = typedClone<SequenceArbitrary<T>>()
        clone.listArbitrary = listArbitrary.uniqueElements(by)
        return clone
    }

}