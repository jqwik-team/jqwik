package net.jqwik.properties;

import net.jqwik.api.*;

public class ObjectArbitrary implements Arbitrary<Object> {
	@Override
	public RandomGenerator<Object> generator(int tries) {
		return random -> {
			Object randomObject = new Object() {
				@Override
				public String toString() {
					return String.format("Arbitrary Object [%d]", random.nextLong());
				}
			};
			return Shrinkable.unshrinkable(randomObject);
		};
	}
}
