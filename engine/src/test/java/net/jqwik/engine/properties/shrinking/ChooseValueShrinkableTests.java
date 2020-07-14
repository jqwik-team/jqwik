package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

class ChooseValueShrinkableTests {

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);

	@Example
	void creation() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(3, Arrays.asList(1, 2, 3, 4, 5));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

	@Example
	void shrinking() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(4, Arrays.asList(1, 2, 3, 4, 5));
		Integer shrunkValue = shrinkToEnd(shrinkable, (TestingFalsifier<Integer>) ignore -> false, valueReporter, null);
		assertThat(shrunkValue).isEqualTo(1);
		verify(valueReporter).accept(1);
		verifyNoMoreInteractions(valueReporter);
	}
}
