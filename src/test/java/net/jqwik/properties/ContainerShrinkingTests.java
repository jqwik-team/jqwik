package net.jqwik.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.arbitraries.*;
import org.assertj.core.api.*;

import net.jqwik.api.*;

@Group
class ContainerShrinkingTests {

	@Group
	class Lists {
		@Example
		void dontShrinkEmptyList() {
			NContainerShrinkable<List<Integer>, Integer> list = emptyShrinkableIntegerList();
			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEmpty();
		}

		private NContainerShrinkable<List<Integer>, Integer> emptyShrinkableIntegerList() {
			return new NContainerShrinkable<>(new ArrayList<>(), ArrayList::new);
		}

		@Example
		void shrinkListSizeOnly() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(0, 0, 0, 0);

			ShrinkResult<Shrinkable<List<Integer>>> shrinkResult = shrink(list, listToShrink -> {
				if (listToShrink.size() < 2)
					return true;
				return false;

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
		void shrinkElementsOnly() {
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
				if (listToShrink.size() < 3)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(0, 0, 0);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
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
			Shrinkable<String> filteredString = new NFilteredShrinkable<>(string, aString -> aString.endsWith("b"));
			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(filteredString,
																   MockFalsifier.falsifyWhen(aString -> aString.length() < 3), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("aab");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(4);
		}

		@Example
		void shrinkIntegerListMappedToString() {
			Shrinkable<List<Integer>> list = ArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);
			Shrinkable<String> string = list.map(aList -> aList.stream() //
															   .map(anInt -> Integer.toString(anInt)) //
															   .collect(Collectors.joining("")));

			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(string, aString -> {
				if (aString.length() < 3)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("000");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Example
		void shrinkTwoIntegersCombinedToString() {
			Arbitrary<Integer> a1 = Arbitraries.integer(0, 5);
			Arbitrary<Integer> a2 = Arbitraries.integer(5, 9);

			Arbitrary<String> combined = Combinators.combine(a1, a2) //
													.as((i1, i2) -> Integer.toString(i1) + Integer.toString(i2));

			Shrinkable<String> stringShrinkable = combined.generator(10).next(new Random(42L));

			ShrinkResult<Shrinkable<String>> shrinkResult = shrink(stringShrinkable, aString -> {
				if (aString.length() < 2)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("05");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(0);
		}
	}

	private <T> ShrinkResult<Shrinkable<T>> shrink(Shrinkable<T> toShrink, Predicate<T> falsifier, Throwable originalError) {
		return new ValueShrinker<T>(toShrink).shrink(falsifier, originalError);
	}
}
