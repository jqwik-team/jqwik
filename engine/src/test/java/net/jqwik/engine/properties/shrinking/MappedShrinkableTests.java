package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("MappedShrinkable")
class MappedShrinkableTests {

	@SuppressWarnings("unchecked")
	private Consumer<String> valueReporter = mock(Consumer.class);

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

		String shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
		assertThat(shrunkValue).isEqualTo("00");
	}

	@Example
	void reportFalsifier() {

		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + i);

		String shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), valueReporter, null);
		assertThat(shrunkValue).isEqualTo("00");

		verify(valueReporter).accept("22");
		verify(valueReporter).accept("11");
		verify(valueReporter).accept("00");
		verifyNoMoreInteractions(valueReporter);
	}

}
