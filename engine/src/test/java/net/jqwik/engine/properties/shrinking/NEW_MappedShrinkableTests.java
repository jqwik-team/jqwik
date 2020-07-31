package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

@Group
@Label("MappedShrinkable")
class NEW_MappedShrinkableTests {

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

		String shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
		assertThat(shrunkValue).isEqualTo("00");
	}

}
