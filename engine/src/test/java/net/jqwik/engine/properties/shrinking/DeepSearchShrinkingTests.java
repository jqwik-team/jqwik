package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("Deep Search Shrinking")
class DeepSearchShrinkingTests {

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);

	@Group
	class ReportFalsified {

		@Example
		@Label("without filter report all current values on the way")
		void withoutFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(4);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 2;
			Integer shrunkValue = shrinkToEnd(shrinkable, falsifier, valueReporter, null);
			assertThat(shrunkValue).isEqualTo(2);

			verify(valueReporter).accept(3);
			verify(valueReporter).accept(2);
			verifyNoMoreInteractions(valueReporter);
		}

		@Example
		@Label("with filter report only simpler values that match filter")
		void withFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(10);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 6;
			Falsifier<Integer> filteredFalsifier = falsifier.withFilter(anInt -> anInt % 2 == 0);
			Integer shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, valueReporter, null);
			assertThat(shrunkValue).isEqualTo(6);

			// 10 is not a new value
			verify(valueReporter, never()).accept(10);
			verify(valueReporter, times(1)).accept(8);
			verify(valueReporter, times(1)).accept(6);
			verifyNoMoreInteractions(valueReporter);
		}

	}

	@Group
	class ShrinkableWithOneStepShrinking {

		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			Integer shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(0);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			TestingFalsifier<Integer> falsifier = anInt -> anInt < 2;
			Integer shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(2);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(10);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 5;
			Falsifier<Integer> filteredFalsifier = falsifier.withFilter(anInt -> anInt % 2 == 0);
			Integer shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(6);
		}

	}

	@Group
	class ShrinkableWithFullShrinking {
		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(5);
			Integer shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(0);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(5);
			TestingFalsifier<Integer> falsifier = anInt -> anInt < 2;

			Integer shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(2);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(10);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 5;
			Falsifier<Integer> filteredFalsifier = falsifier.withFilter(anInt -> anInt % 2 == 0);
			Integer shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(6);
		}

	}

	@Group
	class ShrinkableWithPartialShrinking {
		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(5);

			TestingFalsifier<Integer> falsifier = anInt -> false;
			Integer shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(0);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(5);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 2;
			Integer shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(2);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(10);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 5;
			Falsifier<Integer> filteredFalsifier = falsifier.withFilter(anInt -> anInt % 2 == 0);
			Integer shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(6);
		}

	}

}
