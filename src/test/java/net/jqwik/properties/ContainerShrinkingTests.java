package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.arbitraries.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Group
class ContainerShrinkingTests {

	@Group
	class Lists {
		@Example
		void dontShrinkEmptyList() {
			ContainerShrinkable<List<Integer>, Integer> list = emptyShrinkableIntegerList();
			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEmpty();
		}

		private ContainerShrinkable<List<Integer>, Integer> emptyShrinkableIntegerList() {
			return new ContainerShrinkable<>(new ArrayList<>(), ArrayList::new, 0);
		}

		@Example
		void shrinkListSizeOnly() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(0, 0, 0, 0);

			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> {
				return listToShrink.size() < 2;

			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(0, 0);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(2);
		}

		@Example
		void captureCorrectErrorWhenShrinking() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4);

			AssertionError error = new AssertionError("error");
			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> {
				if (listToShrink.size() < 2)
					return true;
				throw error;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(0, 0);
			Assertions.assertThat(shrinkResult.throwable()).isPresent();
			Assertions.assertThat(shrinkResult.throwable().get()).isSameAs(error);
		}

		@Example
		void ifSizeCannotBeShrunkShrinkElements() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4);

			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> {
				if (listToShrink.size() != 4)
					return true;
				return !listToShrink.stream().allMatch(anInt -> anInt > 0);

			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(1, 1, 1, 1);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(8);
		}

		@Example
		void shrinkNumberOfElementsThenIndividualElements() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);

			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> {
				return listToShrink.size() < 3;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(0, 0, 0);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Property(tries = 100)
		void dontShrinkBelowMinSize(@ForAll @IntRange(min = 1, max = 100) int minSize, @ForAll Random random) {
			RandomGenerator<Integer> integers = RandomGenerators.integers(1, 100);
			RandomGenerator<List<Integer>> lists = RandomGenerators.list(integers, minSize, minSize + 10);

			Shrinkable<List<Integer>> list = lists.sampleRandomly(random);

			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> false, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).hasSize(minSize);
		}

	}

	@Group
	class Strings {
		@Example
		void dontShrinkEmptyString() {
			Shrinkable<String> string = ArbitraryTestHelper.shrinkableString();
			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(string, MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEmpty();
		}

		@Example
		void shrinkStringToOnlyAs() {
			Shrinkable<String> string = ArbitraryTestHelper.shrinkableString("xyzxzy");
			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(string, MockFalsifier.falsifyWhen(aString -> aString.length() < 3),
																   null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("aaa");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Example
		void shrinkFilteredString() {
			Shrinkable<String> string = ArbitraryTestHelper.shrinkableString("xyzxzb");
			Shrinkable<String> filteredString = new FilteredShrinkable<>(string, aString -> aString.endsWith("b"));
			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(filteredString,
																   MockFalsifier.falsifyWhen(aString -> aString.length() < 3), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("aab");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(4);
		}

		@Example
		void shrinkElementOfFilteredStringAlsoIfSizeCouldBeShrinkedInUnfilteredString() {
			Shrinkable<String> string = ArbitraryTestHelper.shrinkableString("x");
			Shrinkable<String> filteredString = new FilteredShrinkable<>(string, aString -> !aString.isEmpty());
			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(filteredString,
																   MockFalsifier.falsifyWhen(aString -> aString.startsWith("a")), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("b");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(2);
		}

		@Example
		void shrinkIntegerListMappedToString() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);
			Shrinkable<String> string = list.map(aList -> aList.stream() //
															   .map(anInt -> Integer.toString(anInt)) //
															   .collect(Collectors.joining("")));

			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(string, aString -> {
				return aString.length() < 3;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("000");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Example
		void shrinkTwoIntegersCombinedToString() {
			Arbitrary<Integer> a1 = Arbitraries.integers().between(0, 5);
			Arbitrary<Integer> a2 = Arbitraries.integers().between(5, 9);

			Arbitrary<String> combined = Combinators.combine(a1, a2) //
													.as((i1, i2) -> Integer.toString(i1) + Integer.toString(i2));

			Shrinkable<String> stringShrinkable = combined.generator(10).next(SourceOfRandomness.current());

			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(stringShrinkable, aString -> {
				return aString.length() < 2;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("05");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(0);
		}

		@Property(tries = 100)
		void dontShrinkBelowMinSize(@ForAll @IntRange(min = 1, max = 100) int minSize, @ForAll Random random) {
			RandomGenerator<Character> characters = RandomGenerators.chars('a', 'b');
			RandomGenerator<String> strings = RandomGenerators.strings(characters, minSize, minSize + 100);

			Shrinkable<String> string = strings.sampleRandomly(random);

			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(string, listToShrink -> false, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).hasSize(minSize);
		}

	}

	private <T> ShrinkResult<Shrinkable<T>> shrink(Shrinkable<T> toShrink, Predicate<T> falsifier, Throwable originalError) {
		return new ValueShrinker<T>(toShrink, ignore -> {}, ShrinkingMode.FULL, ignore -> {}).shrink(falsifier, originalError);
	}
}
