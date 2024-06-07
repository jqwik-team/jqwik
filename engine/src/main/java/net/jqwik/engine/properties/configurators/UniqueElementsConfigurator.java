package net.jqwik.engine.properties.configurators;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

@SuppressWarnings("unchecked")
public class UniqueElementsConfigurator implements ArbitraryConfigurator {

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public <T extends @Nullable Object> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		return targetType.findAnnotation(UniqueElements.class).map(uniqueness -> {
			if (arbitrary instanceof SetArbitrary) {
				// Handle SetArbitrary explicitly for optimization
				return (Arbitrary<T>) configureSetArbitrary((SetArbitrary<?>) arbitrary, uniqueness);
			}
			if (arbitrary instanceof StreamableArbitrary) {
				return (Arbitrary<T>) configureStreamableArbitrary((StreamableArbitrary<?, ?>) arbitrary, uniqueness);
			}
			if (targetType.isAssignableFrom(List.class)) {
				Arbitrary<List<?>> listArbitrary = (Arbitrary<List<?>>) arbitrary;
				return (Arbitrary<T>) listArbitrary.filter(isUnique(uniqueness));
			}
			if (targetType.isAssignableFrom(Set.class)) {
				Arbitrary<Set<?>> setArbitrary = (Arbitrary<Set<?>>) arbitrary;
				return (Arbitrary<T>) setArbitrary.filter(isUnique(uniqueness));
			}
			if (targetType.isArray()) {
				Arbitrary<Object[]> arrayArbitrary = (Arbitrary<Object[]>) arbitrary;
				Predicate<List<?>> predicate = isUnique(uniqueness);
				return (Arbitrary<T>) arrayArbitrary.filter(array -> predicate.test(Arrays.asList(array)));
			}
			if (targetType.isAssignableFrom(Stream.class)) {
				Arbitrary<Stream<?>> streamArbitrary = (Arbitrary<Stream<?>>) arbitrary;
				// Since a stream can only be consumed once this is more involved than seems necessary at first glance
				return (Arbitrary<T>) streamArbitrary.map(s -> s.collect(Collectors.toList()))
													 .filter(isUnique(uniqueness))
													 .map(Collection::stream);
			}
			if (targetType.isAssignableFrom(Iterator.class)) {
				Arbitrary<Iterator<?>> iteratorArbitrary = (Arbitrary<Iterator<?>>) arbitrary;
				// Since an iterator can only be iterated once this is more involved than seems necessary at first glance
				Arbitrary<List<?>> listArbitrary = iteratorArbitrary.map(this::toList);
				return (Arbitrary<T>) listArbitrary.filter(isUnique(uniqueness)).map(List::iterator);
			}
			return arbitrary;
		}).orElse(arbitrary);
	}

	private <T extends @Nullable Object> List<T> toList(Iterator<T> i) {
		List<T> list = new ArrayList<>();
		while (i.hasNext()) {
			list.add(i.next());
		}
		return list;
	}

	private static <C extends Collection<?>> Predicate<C> isUnique(UniqueElements uniqueness) {
		Class<? extends Function<?, Object>> extractorClass = uniqueness.by();
		if (extractorClass.equals(UniqueElements.NOT_SET.class)) {
			return items -> {
				// Intentionally uses `items.getClass().equals(HashSet.class)`
				// instead of `items instanceof HashSet`, because subclasses
				// of HashSet may break Set conventions.
				Class<?> c = items.getClass();
				if (c.equals(HashSet.class) ||
						c.equals(LinkedHashSet.class) ||
						c.equals(ConcurrentHashMap.KeySetView.class) ||
						c.equals(CopyOnWriteArraySet.class))
					return true;
				Set<Object> set = new HashSet<>();
				for (Object x : items) {
					if (!set.add(x)) {
						return false;
					}
				}
				return true;
			};
		}
		Function<Object, Object> extractor = extractor(extractorClass);
		return items -> {
			Set<Object> set = new HashSet<>();
			for (Object x : items) {
				if (!set.add(extractor.apply(x))) {
					return false;
				}
			}
			return true;
		};
	}

	private <T extends @Nullable Object> Arbitrary<?> configureStreamableArbitrary(StreamableArbitrary<T, ?> arbitrary, UniqueElements uniqueness) {
		Class<? extends Function<?, Object>> extractorClass = uniqueness.by();
		if (extractorClass.equals(UniqueElements.NOT_SET.class)) {
			return arbitrary.uniqueElements();
		}
		Function<T, Object> extractor = extractor(extractorClass);
		return arbitrary.uniqueElements(extractor);
	}

	private <T extends @Nullable Object> Arbitrary<?> configureSetArbitrary(SetArbitrary<T> arbitrary, UniqueElements uniqueness) {
		Class<? extends Function<?, Object>> extractorClass = uniqueness.by();
		if (extractorClass.equals(UniqueElements.NOT_SET.class)) {
			return arbitrary;
		}
		Function<T, Object> extractor = extractor(extractorClass);
		return arbitrary.uniqueElements(extractor);
	}

	private static <T extends @Nullable Object> Function<T, Object> extractor(Class<? extends Function<?, Object>> extractorClass) {
		return (Function<T, Object>) (
			extractorClass.equals(UniqueElements.NOT_SET.class)
				? Function.identity()
				// TODO: Create instance in context of test instance.
				//       This requires an extension of ArbitraryConfiguration interface
				//       to provide access to PropertyLifecycleContext
				: JqwikReflectionSupport.newInstanceWithDefaultConstructor(extractorClass)
		);
	}

}
