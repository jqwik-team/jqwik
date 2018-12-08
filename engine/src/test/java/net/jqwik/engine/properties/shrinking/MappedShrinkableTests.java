package net.jqwik.engine.properties.shrinking;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("MappedShrinkable")
class MappedShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	@SuppressWarnings("unchecked")
	private Consumer<String> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<String>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + i);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).isEqualTo("33");
	}

	@Example
	void shrinking() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + i);

		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("22");
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("11");
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("00");
		assertThat(sequence.next(count, reporter)).isFalse();
		assertThat(sequence.current().value()).isEqualTo("00");

		assertThat(counter.get()).isEqualTo(3);
	}


	@Example
	void reportFalsifier() {

		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + i);

		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("22");
		verify(valueReporter).accept("22");


		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("11");
		verify(valueReporter).accept("11");

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("00");
		verify(valueReporter).accept("00");

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(valueReporter);

	}

}
