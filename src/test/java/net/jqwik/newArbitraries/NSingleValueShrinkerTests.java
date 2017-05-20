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
	void shrinkSingletonShrinkSetToLastFalsifiedValue() {
		NShrinkable<Integer> shrinkable = new NShrinkableValue<Integer>(10, value -> Collections.singleton(value - 1));
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 2);
		NSingleValueShrinker<Integer> singleValueShrinker = new NSingleValueShrinker<>(shrinkable);
		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo(3);
	}
}
