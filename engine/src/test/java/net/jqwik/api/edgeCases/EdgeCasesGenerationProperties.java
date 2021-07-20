package net.jqwik.api.edgeCases;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class EdgeCasesGenerationProperties {

	private final List<List<Object>> generated = new ArrayList<>();

	@Property(tries = 20, generation = GenerationMode.RANDOMIZED, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(CheckIntEdgeCasesGeneratedFirst.class)
	void intPropertyEdgeCasesFirst(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class CheckIntEdgeCasesGeneratedFirst implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated(9)).containsExactlyInAnyOrder(
				asList(-2),
				asList(-1),
				asList(0),
				asList(1),
				asList(2),
				asList(-100),
				asList(-99),
				asList(99),
				asList(100)
			);
		}
	}

	@Property(generation = GenerationMode.RANDOMIZED, edgeCases = EdgeCasesMode.MIXIN)
	@PerProperty(CheckIntEdgeCasesMixedIn.class)
	void intPropertyEdgeCasesMixedIn(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class CheckIntEdgeCasesMixedIn implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated).contains(
				asList(-2),
				asList(-1),
				asList(0),
				asList(1),
				asList(2),
				asList(-100),
				asList(100)
			);
		}
	}

	@Property(tries = 5, generation = GenerationMode.RANDOMIZED, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(Check5Tries.class)
	void moreEdgeCasesThanTries(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class Check5Tries implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated).hasSize(5);
			assertThat(generated).doesNotHaveDuplicates();
			assertThat(generated).isSubsetOf(
				asList(-100),
				asList(-2),
				asList(-1),
				asList(0),
				asList(1),
				asList(2),
				asList(100)
			);
		}
	}

	@Property(tries = 100, generation = GenerationMode.RANDOMIZED, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(CheckCombinedIntEdgeCasesFirst.class)
	void twoInts(
		@ForAll @IntRange(min = -2, max = 2) int int1,
		@ForAll @IntRange(min = -2, max = 2) int int2
	) {
		generated.add(asList(int1, int2));
	}

	private class CheckCombinedIntEdgeCasesFirst implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated(25)).contains(
				asList(-2, -2),
				asList(-2, -1),
				asList(-2, -0),
				asList(-2, 1),
				asList(-2, 2),
				asList(-1, -2),
				asList(-1, -1),
				asList(-1, -0),
				asList(-1, 1),
				asList(-1, 2),
				asList(0, -2),
				asList(0, -1),
				asList(0, -0),
				asList(0, 1),
				asList(0, 2),
				asList(1, -2),
				asList(1, -1),
				asList(1, -0),
				asList(1, 1),
				asList(1, 2),
				asList(2, -2),
				asList(2, -1),
				asList(2, -0),
				asList(2, 1),
				asList(2, 2)
			);
		}
	}

	@Property(tries = 1000, edgeCases = EdgeCasesMode.MIXIN)
	@PerProperty(CheckCombinationsIntEvenInt.class)
	void edgeCasesFromFilteredInt(
		@ForAll @IntRange(min = -1, max = 1) int anInt,
		@ForAll("evenNumbers") int evenInt
	) {
		generated.add(asList(anInt, evenInt));
	}

	@Provide
	Arbitrary<Integer> evenNumbers() {
		return Arbitraries.integers().between(-100, 100).filter(i -> i % 2 == 0);
	}

	private class CheckCombinationsIntEvenInt implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated).contains(
					asList(-1, -100),
					asList(-1, -2),
					asList(-1, -0),
					asList(-1, 2),
					asList(-1, 100),
					asList(1, -100),
					asList(1, -2),
					asList(1, -0),
					asList(1, 2),
					asList(1, 100)
			);
		}
	}

	private List<List<Object>> generated(int toIndex) {
		return generated.subList(0, toIndex);
	}

	@Property(tries = 10000, edgeCases = EdgeCasesMode.NONE)
	@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
	void edgeCasesShouldNotBeOverrepresentedWithEdgeCasesSetToNone(@ForAll String aString, @ForAll int anInt) {

		Statistics.label("empty string")
				  .collect(aString.isEmpty())
				  .coverage(checker -> checker.check(true).percentage(p -> p < 1.5));

		Statistics.label("zero int")
				  .collect(anInt == 0)
				  .coverage(checker -> checker.check(true).percentage(p -> p < 0.8));
	}
}


