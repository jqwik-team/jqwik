package net.jqwik.api;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface ExhaustiveGenerator<T> extends Iterable<T> {

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

	default <U> ExhaustiveGenerator<U> map(Function<T, U> mapper) {
		return new MappedExhaustiveGenerator<>(this, mapper);
	}

	default ExhaustiveGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredExhaustiveGenerator<>(this, filterPredicate);
	}

	default ExhaustiveGenerator<T> unique() {
		Set<T> usedValues = ConcurrentHashMap.newKeySet();
		Predicate<T> isUnique = o -> {
			if (usedValues.contains(o)) {
				return false;
			} else {
				usedValues.add(o);
				return true;
			}
		};
		return filter(isUnique);
	}

	default ExhaustiveGenerator<T> injectNull() {
		return new ExhaustiveGenerator<T>() {
			@Override
			public long maxCount() {
				return ExhaustiveGenerator.this.maxCount();
			}

			@Override
			public Iterator<T> iterator() {
				return null;
			}
		};
	}
}
