package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class ValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		NShrinkable<String> unshrinkable = NShrinkable.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		AssertionError originalError = new AssertionError();
		NValueShrinker<String> singleValueShrinker = new NValueShrinker<>(unshrinkable);

		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, originalError);
		assertThat(shrinkResult.shrunkValue()).isSameAs(unshrinkable);
		assertThat(shrinkResult.throwable().get()).isSameAs(originalError);
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(3);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinkable<String> shrinkable = ArbitraryTestHelper.shrinkableString("hello this is a longer sentence.");
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		NValueShrinker<String> singleValueShrinker = new NValueShrinker<>(shrinkable);
		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo("haa");
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkWithAssertionError() {
		NShrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assertions.assertThat(anInt).isEqualTo(0);
			return true;
		};
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(1);
		assertThat(shrinkResult.throwable()).isPresent();
		assertThat(shrinkResult.throwable().get()).isInstanceOf(AssertionError.class);
	}

	@Example
	void shrinkResultsOutsideAssumptionsAreNotConsidered() {
		NShrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assumptions.assumeThat(anInt % 2 == 0);
			return anInt < 3;
		};
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(4);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

}
