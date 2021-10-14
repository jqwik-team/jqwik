package net.jqwik.kotlin.internal;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import kotlin.sequences.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.facades.*;
import net.jqwik.api.providers.*;
import net.jqwik.kotlin.api.*;

@SuppressWarnings("unchecked")
public class KotlinUniqueElementsConfigurator implements ArbitraryConfigurator {

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		return targetType.findAnnotation(UniqueElements.class).map(uniqueness -> {
			if (arbitrary instanceof SequenceArbitrary) {
				return (Arbitrary<T>) configureSequenceArbitrary((SequenceArbitrary<?>) arbitrary, uniqueness);
			}
			if (targetType.isAssignableFrom(Sequence.class)) {
				Arbitrary<Sequence<?>> sequenceArbitrary = (Arbitrary<Sequence<?>>) arbitrary;
				return (Arbitrary<T>) sequenceArbitrary.filter(s -> isUnique(toList(s.iterator()), (Function<Object, Object>) extractor(uniqueness)));
			}
			return arbitrary;
		}).orElse(arbitrary);
	}

	private <T> List<T> toList(Iterator<T> i) {
		List<T> list = new ArrayList<>();
		while (i.hasNext()) {
			list.add(i.next());
		}
		return list;
	}

	private boolean isUnique(Collection<?> list, Function<Object, Object> extractor) {
		Set<Object> set = list.stream().map(extractor).collect(Collectors.toSet());
		return set.size() == list.size();
	}

	private <T> Arbitrary<?> configureSequenceArbitrary(SequenceArbitrary<T> arbitrary, UniqueElements uniqueness) {
		Function<T, Object> extractor = (Function<T, Object>) extractor(uniqueness);
		return arbitrary.uniqueElements(extractor);
	}

	private Function<?, Object> extractor(UniqueElements uniqueness) {
		Class<? extends Function<?, Object>> extractorClass = uniqueness.by();
		return extractorClass.equals(UniqueElements.NOT_SET.class)
				   ? Function.identity()
				   // TODO: Create instance in context of test instance.
				   //       This requires an extension of ArbitraryConfiguration interface
				   //       to provide access to PropertyLifecycleContext
				   : ReflectionSupportFacade.implementation.newInstanceWithDefaultConstructor(extractorClass);
	}

}
