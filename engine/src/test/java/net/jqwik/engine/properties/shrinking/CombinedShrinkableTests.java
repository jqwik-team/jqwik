package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("CombinedShrinkable")
class CombinedShrinkableTests {

	@SuppressWarnings("unchecked")
	private final Consumer<Integer> valueReporter = mock(Consumer.class);

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Example
	void creation() {
		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable hello = Shrinkable.unshrinkable("hello");
		Function<List<Object>, String> combinator = shrinkables -> {
			int anInt = (int) shrinkables.get(0);
			String aString = (String) shrinkables.get(1);
			return aString + anInt;
		};

		List<Shrinkable<Object>> shrinkables = Arrays.asList(three, hello);
		Shrinkable<String> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 0));
		assertThat(shrinkable.value()).isEqualTo("hello3");
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Example
	void shrinking() {
		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable five = new OneStepShrinkable(5);
		Function<List<Object>, Integer> combinator = shrinkables -> {
			int first = (int) shrinkables.get(0);
			int second = (int) shrinkables.get(1);
			return first + second;
		};

		List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
		Shrinkable<Integer> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

		Integer shrunkValue = shrinkToEnd(shrinkable, falsifier(result -> result < 4), null);
		assertThat(shrunkValue).isEqualTo(4);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Example
	void reportFalsifier() {

		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable five = new OneStepShrinkable(5);
		Function<List<Object>, Integer> combinator = shrinkables -> {
			int first = (int) shrinkables.get(0);
			int second = (int) shrinkables.get(1);
			return first + second;
		};

		List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
		Shrinkable<Integer> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

		Integer shrunkValue = shrinkToEnd(shrinkable, falsifier(result -> result < 4), valueReporter, null);
		assertThat(shrunkValue).isEqualTo(4);

		verify(valueReporter).accept(7);
		verify(valueReporter).accept(6);
		verify(valueReporter).accept(5);
		verify(valueReporter).accept(4);

		verifyNoMoreInteractions(valueReporter);
	}

}
