package net.jqwik.engine.properties.configurators;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class UniquenessConfigurator implements ArbitraryConfigurator {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		return targetType.findAnnotation(Uniqueness.class).map(uniqueness -> {
			if (arbitrary instanceof ListArbitrary) {
				ListArbitrary<?> listArbitrary = (ListArbitrary<?>) arbitrary;
				return (Arbitrary<T>) configureListArbitrary(listArbitrary, uniqueness);
			}
			if (targetType.isAssignableFrom(List.class)) {
				Arbitrary<List<?>> listArbitrary = (Arbitrary<List<?>>) arbitrary;
				return (Arbitrary<T>) listArbitrary.filter(l -> isUnique(l, extractor(uniqueness)));
			}
			return arbitrary;
		}).orElse(arbitrary);
	}

	private boolean isUnique(List<?> list, Function<Object, Object> extractor) {
		Set<Object> set = list.stream().map(extractor).collect(Collectors.toSet());
		return set.size() == list.size();
	}

	private Arbitrary<?> configureListArbitrary(ListArbitrary<?> arbitrary, Uniqueness uniqueness) {
		Function<Object, Object> extractor = extractor(uniqueness);
		return arbitrary.uniqueness(extractor::apply);
	}

	private Function<Object, Object> extractor(Uniqueness uniqueness) {
		Class<? extends Function<Object, Object>> extractorClass = uniqueness.by();
		return extractorClass.equals(Uniqueness.NOT_SET.class)
				? Function.identity()
				: JqwikReflectionSupport.newInstanceWithDefaultConstructor(extractorClass);
	}

}
