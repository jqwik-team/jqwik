package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;

import java.util.*;

public class NSingleValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		NShrinkable<String> unshrinkable = NShrinkableValue.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(unshrinkable);

		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo("hello");
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinker<Integer> integerNShrinker = new NShrinker<Integer>() {
			@Override
			public Set<Integer> shrink(Integer value) {
				return Collections.singleton(value - 1);
			}

			@Override
			public int distance(Integer value) {
				return value;
			}
		};
		NShrinkable<Integer> shrinkable = new NShrinkableValue<Integer>(10, integerNShrinker);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		NSingleValueShrinker<Integer> singleValueShrinker = new NSingleValueShrinker<>(shrinkable);
		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo(3);
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinker<String> stringShrinker = new NStringShrinker();

		NShrinkable<String> shrinkable = new NShrinkableValue<String>("hello this is a longer sentence.", stringShrinker);
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(shrinkable);
		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo("hel");
	}
}
