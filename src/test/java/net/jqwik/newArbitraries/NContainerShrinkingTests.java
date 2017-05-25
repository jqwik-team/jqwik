package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

@Group
class NContainerShrinkingTests {

	@Group
	class Lists {
		@Example
		void dontShrinkEmptyList() {
			NContainerShrinkable<List<Integer>, Integer> list = emptyShrinkableIntegerList();
			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEmpty();
		}

		private NContainerShrinkable<List<Integer>, Integer> emptyShrinkableIntegerList() {
			return new NContainerShrinkable<>(new ArrayList<>(), ArrayList::new);
		}

		@Example
		void shrinkListSizeOnly() {
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(0, 0, 0, 0);

			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
				if (listToShrink.size() < 2)
					return true;
				return false;

			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(0, 0);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(2);
		}

		@Example
		void captureCorrectErrorWhenShrinking() {
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4);

			AssertionError error = new AssertionError("error");
			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
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
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4);

			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
				if (listToShrink.size() != 4)
					return true;
				return !listToShrink.stream().allMatch(anInt -> anInt > 0);

			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).containsExactly(1, 1, 1, 1);
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(8);
		}

		@Example
		void shrinkNumberOfElementsThenIndividualElements() {
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);

			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
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
			NShrinkable<String> string = NArbitraryTestHelper.shrinkableString();
			NShrinkResult<NShrinkable<String>> shrinkResult = string.shrink(MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEmpty();
		}

		@Example
		void shrinkStringToOnlyAs() {
			NShrinkable<String> string = NArbitraryTestHelper.shrinkableString("xyzxzy");
			NShrinkResult<NShrinkable<String>> shrinkResult = string.shrink(MockFalsifier.falsifyWhen(aString -> aString.length() < 3),
					null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("aaa");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Example
		void shrinkFilteredString() {
			NShrinkable<String> string = NArbitraryTestHelper.shrinkableString("xyzxzb");
			NShrinkable<String> filteredString = new NFilteredShrinkable<>(string, aString -> aString.endsWith("b"));
			NShrinkResult<NShrinkable<String>> shrinkResult = filteredString
					.shrink(MockFalsifier.falsifyWhen(aString -> aString.length() < 3), null);
			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("aab");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(4);
		}

		@Example
		void shrinkIntegerListMappedToString() {
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);
			NShrinkable<String> string = list.map(aList -> aList.stream() //
					.map(anInt -> Integer.toString(anInt)) //
					.collect(Collectors.joining("")));

			NShrinkResult<NShrinkable<String>> shrinkResult = string.shrink(aString -> {
				if (aString.length() < 3)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("000");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(3);
		}

		@Example
		void shrinkTwoStringsCombined() {
			NArbitrary<String> a1 = NArbitraries.string('a', 'c');
			NArbitrary<String> a2 = NArbitraries.string('d', 'f');

			NArbitrary<String> combined = NCombinators.combine(a1, a2).as((s1, s2) -> s1 + s2);

			NShrinkable<String> stringShrinkable = combined.generator(10).next(new Random());

			NShrinkResult<NShrinkable<String>> shrinkResult = stringShrinkable.shrink(aString -> {
				if (aString.length() < 2)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.shrunkValue().value()).isEqualTo("xx");
			Assertions.assertThat(shrinkResult.shrunkValue().distance()).isEqualTo(2);
		}
	}

}
