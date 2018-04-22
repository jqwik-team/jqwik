package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Group
class FilteredShrinkableTests {

	@Group
	class FalsifyAll {

		private Predicate<Integer> falsifyEverything = anInt -> false;

		@Example
		void filterDoesNothing() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(10);
			FilteredShrinkable<Integer> shrinkable = new FilteredShrinkable<>(integerShrinkable, anInt -> true);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkable.shrinkNext(falsifyEverything);

			Assertions.assertThat(toValues(shrinkResults)).containsExactlyInAnyOrder(0, 8, 9);
		}

		@Example
		void filterDoesSomething() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(10);
			Predicate<Integer> filter = anInt -> anInt > 0;
			FilteredShrinkable<Integer> shrinkable = new FilteredShrinkable<>(integerShrinkable, filter);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkable.shrinkNext(falsifyEverything);

			Assertions.assertThat(toValues(shrinkResults)).containsExactlyInAnyOrder(8, 9);
		}

		@Example
		void doubleFilter() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(40);
			FilteredShrinkable<Integer> shrinkableDivisibleBy5 = new FilteredShrinkable<>(integerShrinkable, anInt -> anInt % 5 == 0);
			FilteredShrinkable<Integer> shrinkableDivisibleBy10 = new FilteredShrinkable<>(shrinkableDivisibleBy5, anInt -> anInt % 2 == 0);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkableDivisibleBy10.shrinkNext(falsifyEverything);

			Set<Integer> shrinkValues = toValues(shrinkResults);
			Assertions.assertThat(shrinkValues).containsExactlyInAnyOrder(0);
		}

	}

	@Group
	class FalsifySome {
		private Predicate<Integer> falsifyOddNumbers = anInt -> anInt % 2 == 0;

		@Example
		void filterDoesNothing() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(10);
			FilteredShrinkable<Integer> shrinkable = new FilteredShrinkable<>(integerShrinkable, anInt -> true);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkable.shrinkNext(falsifyOddNumbers);

			Set<Integer> shrinkValues = toValues(shrinkResults);
			Assertions.assertThat(shrinkValues).containsExactlyInAnyOrder(9);
		}

		@Example
		void filterDoesSomething() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(15);
			Predicate<Integer> filter = anInt -> anInt % 5 == 0;
			FilteredShrinkable<Integer> shrinkable = new FilteredShrinkable<>(integerShrinkable, filter);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkable.shrinkNext(falsifyOddNumbers);

			Set<Integer> shrinkValues = toValues(shrinkResults);
			Assertions.assertThat(shrinkValues).containsExactlyInAnyOrder(5);
		}

		@Example
		void doubleFilter() {
			Shrinkable<Integer> integerShrinkable = shrinkableInteger(75);
			FilteredShrinkable<Integer> shrinkableDivisibleBy5 = new FilteredShrinkable<>(integerShrinkable, anInt -> anInt % 5 == 0);
			FilteredShrinkable<Integer> shrinkableDivisibleBy15 = new FilteredShrinkable<>(shrinkableDivisibleBy5, anInt -> anInt % 3 == 0);

			Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults = shrinkableDivisibleBy15.shrinkNext(falsifyOddNumbers);

			Set<Integer> shrinkValues = toValues(shrinkResults);
			Assertions.assertThat(shrinkValues).containsExactlyInAnyOrder(45);
			// TODO: 15 should also be detected
			// Assertions.assertThat(shrinkValues).containsExactlyInAnyOrder(15, 45);
		}

	}

	private Shrinkable<Integer> shrinkableInteger(int anInt) {
		return new ShrinkableValue<>(anInt, new AnIntegerShrinker());
	}

	private <T> Set<T> toValues(Set<ShrinkResult<Shrinkable<T>>> shrinkResults) {
		return shrinkResults.stream()
							.map(shrinkResult -> shrinkResult.shrunkValue().value())
							.collect(Collectors.toSet());
	}

	private static class AnIntegerShrinker implements ShrinkCandidates<Integer> {
		@Override
		public Set<Integer> nextCandidates(Integer value) {
			// System.out.println(value);
			if (value == 0)
				return Collections.emptySet();
			Set<Integer> candidates = new HashSet<>();
			candidates.add(0);
			candidates.add(value - 1);
			if (value > 1)
				candidates.add(value - 2);

			return candidates;
		}

		@Override
		public int distance(Integer value) {
			return value;
		}
	}

}
