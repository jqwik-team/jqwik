package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

public class ObjectArbitrary implements Arbitrary<Object> {
	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return random -> {
			Object randomObject = new Object() {
				@Override
				public String toString() {
					return String.format("Object[%d]", random.nextLong());
				}
			};
			return Shrinkable.unshrinkable(randomObject);
		};
	}
}
