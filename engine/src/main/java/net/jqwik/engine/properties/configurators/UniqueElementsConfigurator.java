package net.jqwik.engine.properties.configurators;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.support.*;

@SuppressWarnings("unchecked")
public class UniqueElementsConfigurator implements ArbitraryConfigurator {

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		return targetType.findAnnotation(UniqueElements.class).map(uniqueness -> {
			if (arbitrary instanceof ListArbitrary) {
				return (Arbitrary<T>) configureListArbitrary((ListArbitrary<?>) arbitrary, uniqueness);
			}
			if (arbitrary instanceof SetArbitrary) {
				return (Arbitrary<T>) configureSetArbitrary((SetArbitrary<?>) arbitrary, uniqueness);
			}
			if (arbitrary instanceof ArrayArbitrary) {
				return (Arbitrary<T>) configureArrayArbitrary((ArrayArbitrary<?, ?>) arbitrary, uniqueness);
			}
			if (arbitrary instanceof StreamArbitrary) {
				return (Arbitrary<T>) configureStreamArbitrary((StreamArbitrary<?>) arbitrary, uniqueness);
			}
			if (arbitrary instanceof IteratorArbitrary) {
				return (Arbitrary<T>) configureIteratorArbitrary((IteratorArbitrary<?>) arbitrary, uniqueness);
			}
			if (targetType.isAssignableFrom(List.class)) {
				Arbitrary<List<?>> listArbitrary = (Arbitrary<List<?>>) arbitrary;
				return (Arbitrary<T>) listArbitrary.filter(l -> isUnique(l, (Function<Object, Object>) extractor(uniqueness)));
			}
			if (targetType.isAssignableFrom(Set.class)) {
				Arbitrary<Set<?>> setArbitrary = (Arbitrary<Set<?>>) arbitrary;
				return (Arbitrary<T>) setArbitrary.filter(l -> isUnique(l, (Function<Object, Object>) extractor(uniqueness)));
			}
			if (targetType.isArray()) {
				Arbitrary<Object[]> arrayArbitrary = (Arbitrary<Object[]>) arbitrary;
				return (Arbitrary<T>) arrayArbitrary.filter(array -> isUnique(Arrays.asList(array), (Function<Object, Object>) extractor(uniqueness)));
			}
			if (targetType.isAssignableFrom(Stream.class)) {
				Arbitrary<Stream<?>> streamArbitrary = (Arbitrary<Stream<?>>) arbitrary;
				// Since a stream can only be consumed once this is more involved than seems necessary at first glance
				return (Arbitrary<T>) streamArbitrary.map(s -> s.collect(Collectors.toList()))
													 .filter(l -> isUnique(l, (Function<Object, Object>) extractor(uniqueness)))
													 .map(Collection::stream);
			}
			if (targetType.isAssignableFrom(Iterator.class)) {
				Arbitrary<Iterator<?>> iteratorArbitrary = (Arbitrary<Iterator<?>>) arbitrary;
				// Since an iterator can only be iterated once this is more involved than seems necessary at first glance
				Arbitrary<List<?>> listArbitrary = iteratorArbitrary.map(this::toList);
				return (Arbitrary<T>) listArbitrary.filter(l -> isUnique(l, (Function<Object, Object>) extractor(uniqueness))).map(List::iterator);
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
		Set<Object> set = list.stream().map(extractor).collect(CollectorsSupport.toLinkedHashSet());
		return set.size() == list.size();
	}

	private <T> Arbitrary<?> configureListArbitrary(ListArbitrary<T> arbitrary, UniqueElements uniqueness) {
		Function<T, Object> extractor = (Function<T, Object>) extractor(uniqueness);
		return arbitrary.uniqueElements(extractor);
	}

	private <T> Arbitrary<?> configureSetArbitrary(SetArbitrary<T> arbitrary, UniqueElements uniqueness) {
		Function<T, Object> extractor = (Function<T, Object>) extractor(uniqueness);
		return arbitrary.uniqueElements(extractor);
	}

	private <T> Arbitrary<?> configureArrayArbitrary(ArrayArbitrary<T, ?> arbitrary, UniqueElements uniqueness) {
		Function<T, Object> extractor = (Function<T, Object>) extractor(uniqueness);
		return arbitrary.uniqueElements(extractor);
	}

	private <T> Arbitrary<?> configureStreamArbitrary(StreamArbitrary<T> arbitrary, UniqueElements uniqueness) {
		Function<T, Object> extractor = (Function<T, Object>) extractor(uniqueness);
		return arbitrary.uniqueElements(extractor);
	}

	private <T> Arbitrary<?> configureIteratorArbitrary(IteratorArbitrary<T> arbitrary, UniqueElements uniqueness) {
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
				: JqwikReflectionSupport.newInstanceWithDefaultConstructor(extractorClass);
	}

}
