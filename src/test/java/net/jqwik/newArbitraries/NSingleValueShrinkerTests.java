package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.assertThat;

public class NSingleValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		NShrinkable<String> unshrinkable = NShrinkableValue.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		AssertionError originalError = new AssertionError();
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(unshrinkable, originalError);

		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.value()).isSameAs(unshrinkable);
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
		NSingleValueShrinker<Integer> singleValueShrinker = new NSingleValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.value().value()).isEqualTo(3);
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
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<String>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.value().value()).isEqualTo("hel");
		assertThat(shrinkResult.throwable()).isNotPresent();
	}

	@Example
	void shrinkWithAssertionError() {
		NShrinkCandidates<Integer> integerNShrinker = new NShrinkCandidates<Integer>() {
			@Override
			public Set<Integer> nextCandidates(Integer value) {
				if (value > 0)
					return Collections.singleton(value - 1);
				else
					return Collections.emptySet();
			}

			@Override
			public int distance(Integer value) {
				return value;
			}
		};
		NShrinkable<Integer> shrinkable = new NShrinkableValue<Integer>(10, integerNShrinker);
		Predicate<Integer> falsifier = anInt -> {
			Assertions.assertThat(anInt).isEqualTo(0);
			return true;
		};
		NSingleValueShrinker<Integer> singleValueShrinker = new NSingleValueShrinker<>(shrinkable, null);
		NShrinkResult<NShrinkable<Integer>> shrinkResult = singleValueShrinker.shrink(falsifier);
		assertThat(shrinkResult.value().value()).isEqualTo(1);
		assertThat(shrinkResult.throwable()).isPresent();
		assertThat(shrinkResult.throwable().get()).isInstanceOf(AssertionError.class);
	}
}
