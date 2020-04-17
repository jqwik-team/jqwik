package net.jqwik.engine.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static java.util.Arrays.*;

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
			Assertions.assertThat(generated(7)).containsExactlyInAnyOrder(
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

	@Property(tries = 1000, afterFailure = AfterFailureMode.RANDOM_SEED, edgeCases = EdgeCasesMode.MIXIN)
	@PerProperty(CheckIntEdgeCasesMixedIn.class)
	@Report(Reporting.GENERATED)
	void intPropertyEdgeCasesMixedIn(@ForAll @IntRange(min = -100, max = 100) int anInt) {
		generated.add(asList(anInt));
	}

	private class CheckIntEdgeCasesMixedIn implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(generated).contains(
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

	protected List<List<Object>> generated(int toIndex) {
		return generated.subList(0, toIndex);
	}

}


