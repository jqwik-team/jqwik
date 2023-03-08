package net.jqwik.kotlin.internal

import net.jqwik.api.Arbitrary
import net.jqwik.api.configurators.ArbitraryConfigurator
import net.jqwik.api.constraints.UniqueElements
import net.jqwik.api.constraints.UniqueElements.NOT_SET
import net.jqwik.api.facades.ReflectionSupportFacade
import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.SequenceArbitrary
import net.jqwik.kotlin.api.isAssignableFrom
import java.util.function.Function

class KotlinUniqueElementsConfigurator : ArbitraryConfigurator {
    @Suppress("UNCHECKED_CAST")
    override fun <T:Any> configure(arbitrary: Arbitrary<T>, targetType: TypeUsage): Arbitrary<T> {
        return targetType.findAnnotation(UniqueElements::class.java).map { uniqueness ->
            return@map when {
                arbitrary is SequenceArbitrary<*> -> configureSequenceArbitrary(arbitrary, uniqueness)
                targetType.isAssignableFrom(Sequence::class) -> {
                    val sequenceArbitrary = arbitrary as Arbitrary<Sequence<*>>
                    sequenceArbitrary.filter {
                        isUnique(
                            it.toList(),
                            extractor(uniqueness) as Function<Any?, Any>
                        )
                    }
                }
                else -> arbitrary
            } as Arbitrary<T>
        }.orElse(arbitrary)
    }

    private fun isUnique(list: Collection<*>, extractor: Function<Any?, Any>): Boolean {
        val set = list.map { extractor.apply(it) }.toSet()
        return set.size == list.size
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> configureSequenceArbitrary(
        arbitrary: SequenceArbitrary<T>,
        uniqueness: UniqueElements
    ): SequenceArbitrary<T> {
        val extractor = extractor(uniqueness) as Function<T, Any>
        return arbitrary.uniqueElements(extractor)
    }

    private fun extractor(uniqueElements: UniqueElements): Function<*, Any> {
        val extractorClass: Class<out Function<*, Any>> = uniqueElements.by.java
        return if (extractorClass == NOT_SET::class.java) Function.identity()
        // TODO: Create instance in context of test instance.
        //       This requires an extension of ArbitraryConfiguration interface
        //       to provide access to PropertyLifecycleContext
        else ReflectionSupportFacade.implementation.newInstanceWithDefaultConstructor(extractorClass)
    }
}