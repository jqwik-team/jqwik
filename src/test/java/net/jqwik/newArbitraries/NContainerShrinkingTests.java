package net.jqwik.newArbitraries;

import java.util.*;

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
			Assertions.assertThat(shrinkResult.value().value()).isEmpty();
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

			Assertions.assertThat(shrinkResult.value().value()).containsExactly(0, 0);
			Assertions.assertThat(shrinkResult.value().distance()).isEqualTo(2);
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

			Assertions.assertThat(shrinkResult.value().value()).containsExactly(0, 0);
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

			Assertions.assertThat(shrinkResult.value().value()).containsExactly(1, 1, 1, 1);
			Assertions.assertThat(shrinkResult.value().distance()).isEqualTo(8);
		}

		@Example
		void shrinkNumberOfElementsThenIndividualElements() {
			NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);

			NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
				if (listToShrink.size() < 3)
					return true;
				return false;
			}, null);

			Assertions.assertThat(shrinkResult.value().value()).containsExactly(0, 0, 0);
			Assertions.assertThat(shrinkResult.value().distance()).isEqualTo(3);
		}
	}

	@Group
	class Strings {
		@Example
		void dontShrinkEmptyString() {
			NShrinkable<String> string = NArbitraryTestHelper.shrinkableString();
			NShrinkResult<NShrinkable<String>> shrinkResult = string.shrink(MockFalsifier.falsifyAll(), null);
			Assertions.assertThat(shrinkResult.value().value()).isEmpty();
		}

		@Example
		void shrinkStringToOnlyAs() {
			NShrinkable<String> list = NArbitraryTestHelper.shrinkableString("xyzxzy");
			NShrinkResult<NShrinkable<String>> shrinkResult = list.shrink(MockFalsifier.falsifyWhen(string -> string.length() < 3), null);
			Assertions.assertThat(shrinkResult.value().value()).isEqualTo("aaa");
			Assertions.assertThat(shrinkResult.value().distance()).isEqualTo(3);
		}
	}

}
