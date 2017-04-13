package net.jqwik.properties;

import java.util.function.*;

@FunctionalInterface
public interface Generator<T> {

	T next();

	default Generator<T> filter(Predicate<? super T> predicate) {
		Generator<T> original = this;
		return () -> {
			while(true) {
				T value = original.next();
				if (predicate.test(value))
					return value;
			}
		};
	};
}
