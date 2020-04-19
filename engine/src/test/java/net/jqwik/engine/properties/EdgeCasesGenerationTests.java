package net.jqwik.engine.properties;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class EdgeCasesGenerationTests {

	private final List<List<Object>> generated = new ArrayList<>();

	@Property(tries = 20, afterFailure = AfterFailureMode.RANDOM_SEED, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(CheckIntEdgeCasesGeneratedFirst.class)
	void intPropertyEdgeCasesFirst(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class CheckIntEdgeCasesGeneratedFirst implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated(7)).containsExactlyInAnyOrder(
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

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED, edgeCases = EdgeCasesMode.MIXIN)
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

	@Property(tries = 5, edgeCases = EdgeCasesMode.FIRST)
	@PerProperty(Check5Tries.class)
	void moreEdgeCasesThanTries(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class Check5Tries implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated).hasSize(5);
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
	@PerProperty(CheckCombinationsIntOddInt.class)
	void edgeCasesFromFilteredInt(
		@ForAll @IntRange(min = -100, max = 100) int anInt,
		@ForAll("oddNumbers") int oddInt
	) {
		generated.add(asList(anInt, oddInt));
	}

	@Provide
	Arbitrary<Integer> oddNumbers() {
		return Arbitraries.integers().between(-100, 100).filter(i -> i % 2 != 0);
	}

	private class CheckCombinationsIntOddInt implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(generated).contains(
				asList(-100, -1),
				asList(-2, -1),
				asList(-1, -1),
				asList(0, -1),
				asList(1, -1),
				asList(2, -1),
				asList(100, -1),
				asList(-100, 1),
				asList(-2, 1),
				asList(-1, 1),
				asList(0, 1),
				asList(1, 1),
				asList(2, 1),
				asList(100, 1)
			);
		}
	}


	private List<List<Object>> generated(int toIndex) {
		return generated.subList(0, toIndex);
	}

}


