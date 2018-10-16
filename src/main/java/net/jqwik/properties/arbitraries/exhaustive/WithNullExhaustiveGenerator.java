package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

public class WithNullExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> baseGenerator;

	public WithNullExhaustiveGenerator(ExhaustiveGenerator<T> baseGenerator) {
		this.baseGenerator = baseGenerator;
	}

	@Override
	public long maxCount() {
		return baseGenerator.maxCount() + 1;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			boolean nullDelivered = false;
			Iterator<T> iterator = baseGenerator.iterator();

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
