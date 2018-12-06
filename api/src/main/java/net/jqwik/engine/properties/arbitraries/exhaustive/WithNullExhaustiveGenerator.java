package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

public class WithNullExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> base;

	public WithNullExhaustiveGenerator(ExhaustiveGenerator<T> base) {
		this.base = base;
	}

	@Override
	public boolean isUnique() {
		return base.isUnique();
	}

	@Override
	public long maxCount() {
		return base.maxCount() + 1;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			boolean nullDelivered = false;
			Iterator<T> iterator = base.iterator();

			@Override
			public boolean hasNext() {
				if (!nullDelivered) {
					return true;
				}
				return iterator.hasNext();
			}

			@Override
			public T next() {
				if (!nullDelivered) {
					nullDelivered = true;
					return null;
				}
				return iterator.next();
			}
		};
	}
}
