package net.jqwik.properties.newShrinking;

import org.opentest4j.*;

import java.util.function.*;

@FunctionalInterface
public interface Falsifier<T> extends Predicate<T> {

	default Falsifier<T> withFilter(Predicate<T> filter) {
		return t -> {
			if (!filter.test(t)) {
				throw new TestAbortedException();
			}
			return this.test(t);
		};
	}
}
