package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("MappedShrinkable")
class MappedShrinkableTests {

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

		String shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
		assertThat(shrunkValue).isEqualTo("00");
	}

}
