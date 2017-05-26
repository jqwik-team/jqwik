package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.assertThat;

public class NValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		NShrinkable<String> unshrinkable = NShrinkableValue.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		AssertionError originalError = new AssertionError();
		NValueShrinker<String> singleValueShrinker = new NValueShrinker<>(unshrinkable, originalError);

		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.shrunkValue()).isSameAs(unshrinkable);
		assertThat(shrinkResult.throwable().get()).isSameAs(originalError);
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinkCandidates<Integer> integerNShrinker = new NShrinkCandidates<Integer>() {
			@Override
			public Set<Integer> nextCandidates(Integer value) {
				return Collections.singleton(value - 1);
			}

			@Override
			public int distance(Integer value) {
				return value;
			}
		};
		NShrinkable<Integer> shrinkable = new NShrinkableValue<Integer>(10, integerNShrinker);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(3);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		List<NShrinkable<Character>> chars = "hello this is a longer sentence." //
			.chars() //
			.mapToObj(e -> NShrinkableValue.unshrinkable((char) e)) //
			.collect(Collectors.toList());

		NShrinkable<String> shrinkable = NContainerShrinkable.stringOf(chars);
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		NValueShrinker<String> singleValueShrinker = new NValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo("hel");
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkWithAssertionError() {
		NShrinkable<Integer> shrinkable = NArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assertions.assertThat(anInt).isEqualTo(0);
			return true;
		};
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(1);
		assertThat(shrinkResult.throwable()).isPresent();
		assertThat(shrinkResult.throwable().get()).isInstanceOf(AssertionError.class);
	}

	@Example
	void shrinkResultsOutsideAssumptionsAreNotConsidered() {
		NShrinkable<Integer> shrinkable = NArbitraryTestHelper.shrinkableInteger(10);
		Predicate<Integer> falsifier = anInt -> {
			Assume.that(anInt % 2 == 0);
			return anInt < 3;
		};
		NValueShrinker<Integer> singleValueShrinker = new NValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.shrunkValue().value()).isEqualTo(4);
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

}
