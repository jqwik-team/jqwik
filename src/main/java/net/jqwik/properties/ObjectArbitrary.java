package net.jqwik.properties;

public class ObjectArbitrary implements Arbitrary<Object> {
	@Override
	public RandomGenerator<Object> generator(long seed, int tries) {
		return random -> new Object() {
			@Override
			public String toString() {
				return String.format("Arbitrary Object [seed: %d]", seed);
			}
		};
	}
}
