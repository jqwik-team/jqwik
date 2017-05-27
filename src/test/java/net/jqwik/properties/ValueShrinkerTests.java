package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class ValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		Shrinkable<String> unshrinkable = Shrinkable.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		AssertionError originalError = new AssertionError();
		ValueShrinker<String> singleValueShrinker = new ValueShrinker<>(unshrinkable);

		ShrinkResult<Shrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, originalError);
		assertThat(shrinkResult.shrunkValue()).isSameAs(unshrinkable);
		assertThat(shrinkResult.throwable().get()).isSameAs(originalError);
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable);
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(3);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		Shrinkable<String> shrinkable = ArbitraryTestHelper.shrinkableString("hello this is a longer sentence.");
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		ValueShrinker<String> singleValueShrinker = new ValueShrinker<>(shrinkable);
		ShrinkResult<Shrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo("haa");
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkWithAssertionError() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assertions.assertThat(anInt).isEqualTo(0);
			return true;
		};
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable);
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(1);
		assertThat(shrinkResult.throwable()).isPresent();
		assertThat(shrinkResult.throwable().get()).isInstanceOf(AssertionError.class);
	}

	@Example
	void shrinkResultsOutsideAssumptionsAreNotConsidered() {
		Shrinkable<Integer> shrinkable = ArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assumptions.assumeThat(anInt % 2 == 0);
			return anInt < 3;
		};
		ValueShrinker<Integer> singleValueShrinker = new ValueShrinker<>(shrinkable);
		ShrinkResult<Shrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier, null);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(4);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

}
