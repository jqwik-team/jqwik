package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("CombinedShrinkable")
class CombinedShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<Integer>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable hello = Shrinkable.unshrinkable("hello");
		Function<List<Object>, String> combinator = shrinkables -> {
			int anInt = (int) shrinkables.get(0);
			String aString = (String) shrinkables.get(1);
			return aString + anInt;
		};

		@SuppressWarnings("unchecked") List<Shrinkable<Object>> shrinkables = Arrays.asList(three, hello);
		Shrinkable<String> shrinkable = new CombinedShrinkable<>( //
																  shrinkables, combinator);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 0));
		assertThat(shrinkable.value()).isEqualTo("hello3");
	}

	@Example
	void shrinking() {
		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable five = new OneStepShrinkable(5);
		Function<List<Object>, Integer> combinator = shrinkables -> {
			int first = (int) shrinkables.get(0);
			int second = (int) shrinkables.get(1);
			return first + second;
		};

		@SuppressWarnings("unchecked") List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
		Shrinkable<Integer> shrinkable = new CombinedShrinkable<>( //
																   shrinkables, combinator);

		ShrinkingSequence<Integer> sequence = shrinkable.shrink(result -> result < 4);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.next(count, reporter)).isTrue();

		assertThat(sequence.current().value()).isEqualTo(4);
		assertThat(sequence.current().distance()).isEqualTo(ShrinkingDistance.of(0, 4));
	}

	@Example
	void reportFalsifier() {

		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable five = new OneStepShrinkable(5);
		Function<List<Object>, Integer> combinator = shrinkables -> {
			int first = (int) shrinkables.get(0);
			int second = (int) shrinkables.get(1);
			return first + second;
		};

		@SuppressWarnings("unchecked") List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
		Shrinkable<Integer> shrinkable = new CombinedShrinkable<>( //
																   shrinkables, combinator);

		ShrinkingSequence<Integer> sequence = shrinkable.shrink(result -> result < 4);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(7);
		verify(valueReporter).accept(7);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(6);
		verify(valueReporter).accept(6);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(5);
		verify(valueReporter).accept(5);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(4);
		verify(valueReporter).accept(4);

		verifyNoMoreInteractions(valueReporter);
	}

}
