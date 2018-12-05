package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.ShrinkableTypesForTest.*;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("FilteredShrinkable")
class FilteredShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<Integer>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

	@Example
	void shrinking() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

		ShrinkingSequence<Integer> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(3);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(1);
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(counter.get()).isEqualTo(3);
	}


	@Example
	void reportFalsifier() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

		ShrinkingSequence<Integer> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(3);
		verify(valueReporter, never()).accept(3);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(1);
		verify(valueReporter).accept(1);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.next(count, reporter)).isFalse();

		verifyNoMoreInteractions(valueReporter);
	}

}
