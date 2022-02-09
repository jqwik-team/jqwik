package net.jqwik.api

import org.apiguardian.api.API
import java.util.function.Function
import java.util.function.Predicate

/**
 * Used only internally to run and compute exhaustive generation of parameters
 */
@API(status = API.Status.INTERNAL)
interface ExhaustiveGenerator<out T> : Iterable<T> {
    @API(status = API.Status.INTERNAL)
    abstract class ExhaustiveGeneratorFacade {
        abstract fun <T, U> map(self: ExhaustiveGenerator<T>, mapper: Function<in T, out U>): ExhaustiveGenerator<U>
        abstract fun <T> filter(
            self: ExhaustiveGenerator<T>,
            filterPredicate: Predicate<in T>,
            maxMisses: Int
        ): ExhaustiveGenerator<T>

        abstract fun <T> injectNull(self: ExhaustiveGenerator<T>): ExhaustiveGenerator<T>
        abstract fun <T> ignoreException(
            self: ExhaustiveGenerator<T>,
            exceptionType: Class<out Throwable>
        ): ExhaustiveGenerator<T>

        companion object {
            val implementation = FacadeLoader.load(
                ExhaustiveGeneratorFacade::class.java
            )
        }
    }

    /**
     * @return the maximum number of values that will be generated
     */
    fun maxCount(): Long
    fun <U> map(mapper: Function<in T, out U>): ExhaustiveGenerator<U> {
        return ExhaustiveGeneratorFacade.implementation!!.map(this, mapper)
    }

    fun filter(filterPredicate: Predicate<in T>, maxMisses: Int): ExhaustiveGenerator<T> {
        return ExhaustiveGeneratorFacade.implementation!!.filter(this, filterPredicate, maxMisses)
    }

    fun injectNull(): ExhaustiveGenerator<T> {
        return ExhaustiveGeneratorFacade.implementation!!.injectNull(this)
    }

    fun ignoreException(exceptionType: Class<out Throwable>): ExhaustiveGenerator<T> {
        return ExhaustiveGeneratorFacade.implementation!!.ignoreException(this, exceptionType)
    }

    companion object {
        const val MAXIMUM_SAMPLES_TO_GENERATE = Int.MAX_VALUE.toLong()
    }
}