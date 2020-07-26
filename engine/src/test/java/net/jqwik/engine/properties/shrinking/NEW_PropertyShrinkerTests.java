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

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

class NEW_PropertyShrinkerTests {

	@SuppressWarnings("unchecked")
	private final Consumer<FalsifiedSample> falsifiedSampleReporter = Mockito.mock(Consumer.class);

	@Group
	class NoShrinking {
		@Example
		void ifParametersAreUnshrinkableReturnOriginalValue() {
			List<Shrinkable<Object>> unshrinkableParameters = asList(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable("hello"));
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(unshrinkableParameters, originalError);
			NEW_PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);
			ShrunkFalsifiedSample sample = shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			assertThat(sample.equivalentTo(originalSample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ifShrinkingIsOffReturnOriginalValue() {
			List<Shrinkable<Object>> parameters = listOfOneStepShrinkables(5, 10);
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(parameters, originalError);
			NEW_PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.OFF);
			ShrunkFalsifiedSample sample = shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			assertThat(sample.equivalentTo(originalSample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ifNothingCanBeShrunkReturnOriginalValue() {
			List<Shrinkable<Object>> parameters = listOfOneStepShrinkables(10);
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(parameters, originalError);
			NEW_PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 9);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.equivalentTo(originalSample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ignoreShrinkingSuggestionsWithShrinkingDistanceNotBelowCurrentBest() {
			ShrinkableWithCandidates shrinkableWithCandidates = new ShrinkableWithCandidates(10, Collections::singleton);
			List<Shrinkable<Object>> parameters = asList(shrinkableWithCandidates.asGeneric());
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(parameters, originalError);
			NEW_PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = ignore -> TryExecutionResult.falsified(null);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.equivalentTo(originalSample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}
	}

	@Group
	class ShrinkSingleParameter {

		@Example
		void stepByStep() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10);

			NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 1);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(8);
		}

		@Example
		void inOneStep() {
			List<Shrinkable<Object>> shrinkables = listOfFullShrinkables(10);

			NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 1);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(1);
		}

		@Example
		void stepByStepWithFilter() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10);

			NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> {
				Assume.that(i % 2 == 0);
				return i <= 1;
			});

			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(4);
		}

	}

	@Example
	void shrinkAllParametersOneAfterTheOther() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer integer1, Integer integer2) -> {
			if (integer1 == 0) return true;
			return integer2 <= 1;
		});
		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).isEqualTo(asList(1, 2));
		assertThat(sample.falsifyingError()).isNotPresent();
		assertThat(sample.countShrinkingSteps()).isGreaterThan(0);

		List<Object> freshParameters = sample.shrinkables().stream().map(Shrinkable::value).collect(Collectors.toList());
		assertThat(freshParameters).containsExactly(1, 2);
	}

	@Example
	void reportFalsifiedParameters() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);
		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);
		shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

		verify(falsifiedSampleReporter, times(15)).accept(any(FalsifiedSample.class));
	}

	@Example
	void falsifyingErrorComesFromActualShrunkValue() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, failAndCatch("original")), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer integer1, Integer integer2) -> {
			if (integer1 == 0) return true;
			if (integer2 <= 1) return true;
			throw failAndCatch("shrinking");
		});
		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).isEqualTo(asList(1, 2));
		assertThat(sample.falsifyingError()).isPresent();
		assertThat(sample.falsifyingError().get()).hasMessage("shrinking");
	}

	@Example
	void sampleParametersAreTheRealOnes() {
		List<Shrinkable<Object>> shrinkables = asList(
			new ShrinkableList<Integer>(asList(new FullShrinkable(42)), 1).asGeneric()
		);

		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = paramFalsifier((List<Integer> list) -> {
			list.add(101);
			return false;
		});
		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).containsExactly(asList(0, 101));
		assertThat(createValues(sample)).containsExactly(asList(0));
	}

	private List<Object> createValues(FalsifiedSample sample) {
		return sample.shrinkables().stream().map(Shrinkable::createValue).collect(Collectors.toList());
	}

	@Disabled("new shrinking")
	@Example
	void differentErrorTypeDoesNotCountAsSameError() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(50);

		AssertionError originalError = failAndCatch("original error");
		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, originalError), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer integer) -> {
			if (integer <= 10) return true;
			if (integer % 2 == 0) {
				throw failAndCatch("shrinking");
			} else {
				throw new IllegalArgumentException();
			}
		});

		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).isEqualTo(asList(12));
		assertThat(sample.falsifyingError().get()).hasMessage("shrinking");
	}

	@Disabled("new shrinking")
	@Example
	void differentErrorStackTraceDoesNotCountAsSameError() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(50);
		AssertionError originalError = failAndCatch("original error");
		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, originalError), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer integer) -> {
			if (integer <= 10) return true;
			if (integer % 2 == 0) {
				throw failAndCatch("shrinking");
			} else {
				throw new RuntimeException("different location");
			}
		});

		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).isEqualTo(asList(12));
		assertThat(sample.falsifyingError().get()).hasMessage("shrinking");
	}

	@Disabled("new shrinking")
	@Example
	void resultSampleConsistsOfActualUsedObjects_notOfValuesGeneratedByShrinkable() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

		TestingFalsifier<List<Object>> falsifier = params -> {
			params.add(42);
			if (((int) params.get(0)) == 0) return true;
			if (((int) params.get(1)) <= 1) return true;
			return false;
		};
		ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

		assertThat(sample.parameters()).isEqualTo(asList(1, 2, 42));
	}

	@Disabled("new shrinking")
	@Example
	void withBoundedShrinkingBreakOffAfter1000Steps() {
		List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(900, 1000);

		NEW_PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.BOUNDED);

		ShrunkFalsifiedSample sample = shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

		assertThat(sample.parameters()).isEqualTo(asList(0, 900));

		// TODO: Test that logging shrinking bound reached has happended
	}

	@Property(tries = 100, edgeCases = EdgeCasesMode.NONE)
	@Disabled("new shrinking")
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

	private List<Shrinkable<Object>> listOfOneStepShrinkables(int... args) {
		return Arrays.stream(args).mapToObj(i -> new OneStepShrinkable(i).asGeneric()).collect(Collectors.toList());
	}

	private List<Shrinkable<Object>> listOfFullShrinkables(int... args) {
		return Arrays.stream(args).mapToObj(i -> new FullShrinkable(i).asGeneric()).collect(Collectors.toList());
	}

	private NEW_PropertyShrinker createShrinker(final FalsifiedSample originalSample, final ShrinkingMode full) {
		return new NEW_PropertyShrinker(
			originalSample,
			full,
			falsifiedSampleReporter,
			null
		);
	}

	// @Group
	// class Duplicates {
	//
	// 	@Property(tries = 10000)
	// 	@ExpectFailure(checkResult = ShrinkTo77.class)
	// 	boolean shrinkDuplicateIntegersTogether(
	// 		@ForAll @IntRange(min = 1, max = 100) int int1,
	// 		@ForAll @IntRange(min = 1, max = 100) int int2
	// 	) {
	// 		return int1 < 7 || int1 != int2;
	// 	}
	//
	// 	private class ShrinkTo77 extends ShrinkToChecker {
	// 		@Override
	// 		public Iterable<?> shrunkValues() {
	// 			return Arrays.asList(7, 7);
	// 		}
	// 	}
	//
	// 	@Property(tries = 10000)
	// 	@ExpectFailure(checkResult = ShrunkToAA.class)
	// 	void shrinkingDuplicateStringsTogether(@ForAll("aString") String first, @ForAll("aString") String second) {
	// 		assertThat(first).isNotEqualTo(second);
	// 	}
	//
	// 	private class ShrunkToAA extends ShrinkToChecker {
	// 		@Override
	// 		public Iterable<?> shrunkValues() {
	// 			return Arrays.asList("aa", "aa");
	// 		}
	// 	}
	//
	// 	@Provide
	// 	Arbitrary<String> aString() {
	// 		return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(2).ofMaxLength(5);
	// 	}
	// }

}
