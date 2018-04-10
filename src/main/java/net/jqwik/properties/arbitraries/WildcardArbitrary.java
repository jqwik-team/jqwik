package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;

public class WildcardArbitrary implements Arbitrary<Object> {
	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return RandomGenerators.integers(0, genSize).map(integer -> new WildcardObject(integer));
	}

	private static class WildcardObject {
		private final Integer index;

		public WildcardObject(Integer index) {
			this.index = index;
		}

		@Override
		public String toString() {
			return String.format("Any[%d]", index);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			WildcardObject that = (WildcardObject) o;
			return Objects.equals(index, that.index);
		}

		@Override
		public int hashCode() {
			return Objects.hash(index);
		}
	}
}
