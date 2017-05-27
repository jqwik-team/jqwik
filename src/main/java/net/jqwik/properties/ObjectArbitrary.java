package net.jqwik.properties;

public class ObjectArbitrary implements NArbitrary<Object> {
	@Override
	public NShrinkableGenerator<Object> generator(int tries) {
		return random -> {
			Object randomObject = new Object() {
				@Override
				public String toString() {
					return String.format("Arbitrary Object [%d]", random.nextLong());
				}
			};
			return NShrinkable.unshrinkable(randomObject);
		};
	}
}
