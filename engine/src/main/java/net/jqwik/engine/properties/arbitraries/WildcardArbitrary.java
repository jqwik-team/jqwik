package net.jqwik.engine.properties.arbitraries;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class WildcardArbitrary implements Arbitrary<Object> {
	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return RandomGenerators.integers(0, genSize/2).map(WildcardObject::new);
	}

	@Override
	public EdgeCases<Object> edgeCases() {
		return EdgeCases.none();
	}

	public static class WildcardObject implements Comparable<WildcardObject>, Serializable {
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

		@Override
		public int compareTo(WildcardObject o) {
			return Integer.compare(this.index, o.index);
		}
	}
}
