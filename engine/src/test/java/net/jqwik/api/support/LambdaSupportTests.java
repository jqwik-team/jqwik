package net.jqwik.api.support;

import java.io.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class LambdaSupportTests {

	@Example
	void sameFunctionsAreEqual() {
		Function<String, String> f = s -> s;
		assertThat(LambdaSupport.areEqual(
			f,
			f
		)).isTrue();
	}

	@Example
	void separateFunctionsAreNotEqual() {
		Function<String, String> f = s -> s;
		Function<String, String> g = s -> s;
		assertThat(LambdaSupport.areEqual(
			f,
			g
		)).isFalse();
	}

	@Example
	void sameFunctionsWithImmutableClosureAreEqual() {
		int added = 5;
		Function<Integer, Integer> f = n -> n + added;
		assertThat(LambdaSupport.areEqual(
			f,
			f
		)).isTrue();
	}

	@Example
	void identityIsEqualToItself() {
		assertThat(LambdaSupport.areEqual(
			Function.identity(),
			Function.identity()
		)).isTrue();
	}

	@Example
	void serializableFunctionsCanBeCompared() {
		assertThat(LambdaSupport.areEqual(
			new SerializableAdder(5),
			new SerializableAdder(5)
		)).isTrue();

		assertThat(LambdaSupport.areEqual(
			new SerializableAdder(5),
			new SerializableAdder(6)
		)).isFalse();
	}

	private static class SerializableAdder implements Function<Integer, Integer>, Serializable {

		private final int added;

		private SerializableAdder(int added) {
			this.added = added;
		}

		@Override
		public Integer apply(Integer i) {
			return i + added;
		}
	}

	/**
	 * See https://github.com/jlink/jqwik/issues/393 for details and motivation.
	 * The failure occurred in jqwik 1.7.0 with Java >= 17.
	 * The critical code is in LambdaSupport.fieldIsEqualIn(..).
	 */
	@Group
	class InaccessibleReflection {

		@Example
		void usedToFailBecauseDifferentArbitraryInstancesWithSameDataAndNegated() {
			String first = Arbitraries.of("NL", "DE", "BE").filter(not("BE"::equals)).sample();
			String second = Arbitraries.of("NL", "DE", "BE").filter(not("BE"::equals)).sample();
		}

		@Property(tries = 100, generation = GenerationMode.RANDOMIZED)
		void useUncachedGeneratorInstances(@ForAll("isoCodeOne") String iso1, @ForAll("isoCodeTwo") String iso2) {
			// System.out.println(iso1 + " " + iso2);
		}

		@Provide
		Arbitrary<String> isoCodeOne() {
			return Arbitraries.of("NL", "DE", "BE").filter(not("BE"::equals));
		}

		@Provide
		Arbitrary<String> isoCodeTwo() {
			return Arbitraries.of("NL", "DE", "BE").filter(not("BE"::equals));
		}

		@Example
		void succeedsBecauseDifferentArbitraryInstancesWithSameDataButNotNegated() {
			String first = Arbitraries.of("NL", "DE", "BE").filter("BE"::equals).sample();
			String second = Arbitraries.of("NL", "DE", "BE").filter("BE"::equals).sample();
		}
	}

	@SuppressWarnings("unchecked")
	static <T> Predicate<T> not(Predicate<? super T> target) {
		Objects.requireNonNull(target);
		return (Predicate<T>) target.negate();
	}

}
