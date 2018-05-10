package net.jqwik.properties.newShrinking;

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
		NShrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(3, Arrays.asList(1, 2, 3, 4, 5));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

//	@Example
//	void shrinking() {
//		NShrinkable<Integer> integerShrinkable = new ShrinkableTypesForTest.OneStepShrinkable(3);
//		NShrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + String.valueOf(i));
//
//		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);
//
//		assertThat(sequence.next(count, ignore -> {
//		})).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("22");
//		assertThat(sequence.next(count, ignore -> {
//		})).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("11");
//		assertThat(sequence.next(count, ignore -> {
//		})).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("00");
//		assertThat(sequence.next(count, ignore -> {
//		})).isFalse();
//		assertThat(sequence.current().value()).isEqualTo("00");
//
//		assertThat(counter.get()).isEqualTo(3);
//	}
//
//
//	@Example
//	void reportFalsifier() {
//
//		Consumer<String> reporter = mock(Consumer.class);
//
//		NShrinkable<Integer> integerShrinkable = new ShrinkableTypesForTest.OneStepShrinkable(3);
//		NShrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + String.valueOf(i));
//
//		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("22");
//		verify(reporter).accept("22");
//
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("11");
//		verify(reporter).accept("11");
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("00");
//		verify(reporter).accept("00");
//
//		assertThat(sequence.next(count, reporter)).isFalse();
//		verifyNoMoreInteractions(reporter);
//
//	}

}
