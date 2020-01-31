package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.reporting.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyShrinkerTests {

	@SuppressWarnings("unchecked")
	private Consumer<ReportEntry> reporter = Mockito.mock(Consumer.class);

	@Example
	void ifThereIsNothingToShrinkReturnOriginalValue() {
		List<Shrinkable<Object>> unshrinkableParameters = asList(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable("hello"));
		PropertyShrinker shrinker = new PropertyShrinker(unshrinkableParameters, ShrinkingMode.FULL, reporter, new Reporting[0]);

		Throwable originalError = new RuntimeException("original error");
		PropertyShrinkingResult result = shrinker.shrink(ignore -> false, originalError);

		assertThat(result.values()).isEqualTo(asList(1, "hello"));
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		verifyNoInteractions(reporter);
	}

	@Example
	void ifShrinkingIsOffReturnOriginalValue() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.OFF, reporter, new Reporting[0]);

		Throwable originalError = new RuntimeException("original error");
		PropertyShrinkingResult result = shrinker.shrink(ignore -> false, originalError);

		assertThat(result.values()).isEqualTo(asList(5, 10));
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		verifyNoInteractions(reporter);
	}

	@Example
	void shrinkAllParameters() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, new Reporting[0]);

		Falsifier<List<Object>> listFalsifier = params -> {
			if (((int) params.get(0)) == 0) return true;
			return ((int) params.get(1)) <= 1;
		};
		PropertyShrinkingResult result = shrinker.shrink(listFalsifier, null);

		assertThat(result.values()).isEqualTo(asList(1, 2));
		assertThat(result.throwable()).isNotPresent();

		assertThat(result.steps()).isEqualTo(12);

		verifyNoInteractions(reporter);
	}

	@Property(tries = 10000)
	@ExpectFailure(checkResult = ShrinkTo77.class)
	boolean shrinkDuplicateParametersTogether(
		@ForAll @Positive int int1,
		@ForAll @Positive int int2
	) {
		return int1 < 7 || int1 != int2;
	}

	private class ShrinkTo77 extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return Arrays.asList(7, 7);
		}
	}

	@Property(tries = 10000, afterFailure = AfterFailureMode.RANDOM_SEED)
	@ExpectFailure(checkResult = ShrunkToAA.class)
	void shrinkBothParametersToStringAA(@ForAll("aString") String first, @ForAll("aString") String second) {
		assertThat(first).isNotEqualTo(second);
	}

	private class ShrunkToAA extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return Arrays.asList("aa", "aa");
		}
	}

	@Provide
	Arbitrary<String> aString() {
		return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(2).ofMaxLength(5);
	}

	@Example
	void reportFalsifiedParameters() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, new Reporting[]{Reporting.FALSIFIED});
		shrinker.shrink(ignore -> false, null);

		verify(reporter, times(15)).accept(any(ReportEntry.class));
	}

	@Example
	void resultThrowableComesFromActualShrinkedValue() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, new Reporting[0]);

		Falsifier<List<Object>> listFalsifier = params -> {
			if (((int) params.get(0)) == 0) return true;
			if (((int) params.get(1)) <= 1) return true;
			throw new RuntimeException(String.format("%s:%s", params.get(0), params.get(1)));
		};
		PropertyShrinkingResult result = shrinker.shrink(listFalsifier, null);

		assertThat(result.values()).isEqualTo(asList(1, 2));
		assertThat(result.throwable()).isPresent();
	}

	@Example
	void withBoundedShrinkingBreakOffAfter1000Steps() {
		List<Shrinkable<Object>> parameters = toList(900, 1000);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.BOUNDED, reporter, new Reporting[0]);

		PropertyShrinkingResult result = shrinker.shrink(ignore -> false, null);

		assertThat(result.values()).isEqualTo(asList(0, 900));

		ArgumentCaptor<ReportEntry> entryCaptor = ArgumentCaptor.forClass(ReportEntry.class);
		verify(reporter, times(1)).accept(entryCaptor.capture());

		assertThat(entryCaptor.getValue().getKeyValuePairs()).containsKeys("shrinking bound reached");
	}

	private List<Shrinkable<Object>> toList(int i, int i2) {
		return asList(
			new OneStepShrinkable(i).asGeneric(),
			new OneStepShrinkable(i2).asGeneric()
		);
	}

}
