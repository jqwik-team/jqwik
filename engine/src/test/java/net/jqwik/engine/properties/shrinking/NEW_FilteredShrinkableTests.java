package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

@Group
@Label("FilteredShrinkable")
class NEW_FilteredShrinkableTests {

	@Example
	void creation() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3));
		assertThat(shrinkable.createValue()).isEqualTo(3);
	}

	@Group
	class Shrinking {

		@Example
		void noStepShrinking() {
			Shrinkable<Integer> integerShrinkable = new FullShrinkable(3);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

			Integer shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(1);
		}

		@Example
		void singleStepShrinking() {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

			Integer shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(1);
		}

		@Example
		void manyStepsShrinking() {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(40);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 5 == 0);

			Integer shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(0);
		}
	}

}
