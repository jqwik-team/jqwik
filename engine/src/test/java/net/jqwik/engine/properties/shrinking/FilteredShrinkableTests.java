package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("FilteredShrinkable")
class FilteredShrinkableTests {

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);

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

		Integer shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
		assertThat(shrunkValue).isEqualTo(1);
	}

	@Example
	void reportFalsifier() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

		Integer shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), valueReporter, null);
		assertThat(shrunkValue).isEqualTo(1);

		verify(valueReporter, never()).accept(3);
		verify(valueReporter).accept(1);
		verifyNoMoreInteractions(valueReporter);
	}

}
