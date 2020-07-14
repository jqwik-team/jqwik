package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

class PropertyShrinkerTests {

	private final Reporter reporter = Mockito.mock(Reporter.class);
	@SuppressWarnings("unchecked")
	private final Consumer<List<Object>> falsifiedSampleReporter = Mockito.mock(Consumer.class);

	@Example
	void ifThereIsNothingToShrinkReturnOriginalValue() {
		List<Shrinkable<Object>> unshrinkableParameters = asList(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable("hello"));
		PropertyShrinker shrinker = new PropertyShrinker(unshrinkableParameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);

		Throwable originalError = failAndCatch("original error");
		PropertyShrinkingResult result = shrinker.shrink(ignore -> TryExecutionResult.falsified(null), originalError);

		assertThat(result.sample()).isEqualTo(asList(1, "hello"));
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		verifyNoInteractions(falsifiedSampleReporter);
	}

	@Example
	void ifShrinkingIsOffReturnOriginalValue() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.OFF, reporter, falsifiedSampleReporter);

		Throwable originalError = failAndCatch("original error");
		PropertyShrinkingResult result = shrinker.shrink(ignore -> TryExecutionResult.falsified(null), originalError);

		assertThat(result.sample()).isEqualTo(asList(5, 10));
		assertThat(result.steps()).isEqualTo(0);
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).isSameAs(originalError);

		verifyNoInteractions(falsifiedSampleReporter);
	}

	@Example
	void shrinkAllParameters() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);

		TestingFalsifier<List<Object>> falsifier = falsifier((Integer integer1, Integer integer2) -> {
			if (integer1 == 0) return true;
			return integer2 <= 1;
		});
		PropertyShrinkingResult result = shrinker.shrink(falsifier, null);

		assertThat(result.sample()).isEqualTo(asList(1, 2));
		assertThat(result.throwable()).isNotPresent();

		assertThat(result.steps()).isEqualTo(12);
	}

	@Property(tries = 100, edgeCases = EdgeCasesMode.NONE)
	@ExpectFailure(checkResult = ShrinkToEmptyList0.class)
	boolean shrinkDependentParameters(
		@ForAll @Size(min = 0, max = 10) List<Integer> list,
		@ForAll @IntRange(min = 0, max = 100) int size
	) {
		return list.size() < size;
	}

	private class ShrinkToEmptyList0 extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return Arrays.asList(Collections.emptyList(), 0);
		}
	}

	@Example
	void reportFalsifiedParameters() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);
		shrinker.shrink(ignore -> TryExecutionResult.falsified(null), null);

		verify(falsifiedSampleReporter, times(15)).accept(any(List.class));
	}

	@Example
	void resultThrowableComesFromActualShrunkValue() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);

		TestingFalsifier<List<Object>> falsifier = falsifier((Integer integer1, Integer integer2) -> {
			if (integer1 == 0) return true;
			if (integer2 <= 1) return true;
			throw failAndCatch("shrinking");
		});
		PropertyShrinkingResult result = shrinker.shrink(falsifier, failAndCatch("original"));

		assertThat(result.sample()).isEqualTo(asList(1, 2));
		assertThat(result.throwable()).isPresent();
		assertThat(result.throwable().get()).hasMessage("shrinking");
	}

	@Example
	void differentErrorTypeDoesNotCountAsSameError() {
		List<Shrinkable<Object>> parameters = toList(50);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);
		AssertionError originalError = failAndCatch("original error");

		TestingFalsifier<List<Object>> falsifier = falsifier((Integer integer) -> {
			if (integer <= 10) return true;
			if (integer % 2 == 0) {
				throw failAndCatch("shrinking");
			} else {
				throw new IllegalArgumentException();
			}
		});

		PropertyShrinkingResult result = shrinker.shrink(falsifier, originalError);

		assertThat(result.sample()).isEqualTo(asList(12));
		assertThat(result.throwable().get()).hasMessage("shrinking");
	}

	@Example
	void differentErrorStackTraceDoesNotCountAsSameError() {
		List<Shrinkable<Object>> parameters = toList(50);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);
		AssertionError originalError = failAndCatch("original error");

		TestingFalsifier<List<Object>> falsifier = falsifier((Integer integer) -> {
			if (integer <= 10) return true;
			if (integer % 2 == 0) {
				throw failAndCatch("shrinking");
			} else {
				throw new RuntimeException("different location");
			}
		});

		PropertyShrinkingResult result = shrinker.shrink(falsifier, originalError);

		assertThat(result.sample()).isEqualTo(asList(12));
		assertThat(result.throwable().get()).hasMessage("shrinking");
	}

	@Example
	void resultSampleConsistsOfActualUsedObjects_notOfValuesGeneratedByShrinkable() {
		List<Shrinkable<Object>> parameters = toList(5, 10);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporter, falsifiedSampleReporter);

		TestingFalsifier<List<Object>> falsifier = params -> {
			params.add(42);
			if (((int) params.get(0)) == 0) return true;
			if (((int) params.get(1)) <= 1) return true;
			return false;
		};
		PropertyShrinkingResult result = shrinker.shrink(falsifier, null);

		assertThat(result.sample()).isEqualTo(asList(1, 2, 42));
	}

	@Example
	void withBoundedShrinkingBreakOffAfter1000Steps() {
		List<Shrinkable<Object>> parameters = toList(900, 1000);

		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.BOUNDED, reporter, falsifiedSampleReporter);

		PropertyShrinkingResult result = shrinker.shrink(ignore -> TryExecutionResult.falsified(null), null);

		assertThat(result.sample()).isEqualTo(asList(0, 900));

		verify(reporter, times(1)).publishValue(eq("shrinking bound reached"), anyString());
	}

	private List<Shrinkable<Object>> toList(int... args) {
		return Arrays.stream(args).mapToObj(i -> new OneStepShrinkable(i).asGeneric()).collect(Collectors.toList());
	}

	@Group
	class Duplicates {

		@Property(tries = 10000)
		@ExpectFailure(checkResult = ShrinkTo77.class)
		boolean shrinkDuplicateIntegersTogether(
			@ForAll @IntRange(min = 1, max = 100) int int1,
			@ForAll @IntRange(min = 1, max = 100) int int2
		) {
			return int1 < 7 || int1 != int2;
		}

		private class ShrinkTo77 extends ShrinkToChecker {
			@Override
			public Iterable<?> shrunkValues() {
				return Arrays.asList(7, 7);
			}
		}

		@Property(tries = 10000)
		@ExpectFailure(checkResult = ShrunkToAA.class)
		void shrinkingDuplicateStringsTogether(@ForAll("aString") String first, @ForAll("aString") String second) {
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
	}

}
