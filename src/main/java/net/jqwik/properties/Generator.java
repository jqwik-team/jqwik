package net.jqwik.properties;

import java.util.function.*;

@FunctionalInterface
public interface Generator<T> {

	T next();

	default Generator<T> filter(Predicate<? super T> predicate) {
		return () -> {
			while(true) {
				T value = Generator.this.next();
				if (predicate.test(value))
					return value;
			}
		};
	};
}
