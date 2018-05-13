package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseValueShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<Integer> reporter = mock(Consumer.class);

	@Example
	void creation() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(3, Arrays.asList(1, 2, 3, 4, 5));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

	@Example
	void shrinking() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(4, Arrays.asList(1, 2, 3, 4, 5));

		ShrinkingSequence<Integer> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.nextValue(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(1);
		verify(reporter).accept(1);

		assertThat(sequence.nextValue(count, reporter)).isFalse();
		assertThat(counter.get()).isEqualTo(1);
		verifyNoMoreInteractions(reporter);
	}
}
