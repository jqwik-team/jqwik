package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.testing.TestingFalsifier.*;

class PropertyShrinkerTests {

	@SuppressWarnings("unchecked")
	private final Consumer<FalsifiedSample> falsifiedSampleReporter = Mockito.mock(Consumer.class);

	@Group
	class NoShrinking {
		@Example
		void ifParametersAreUnshrinkableReturnOriginalValue() {
			List<Shrinkable<Object>> unshrinkableParameters = asList(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable("hello"));
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(unshrinkableParameters, originalError);
			PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);
			ShrunkFalsifiedSample sample = shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			assertThat(originalSample.equals(sample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ifShrinkingIsOffReturnOriginalValue() {
			List<Shrinkable<Object>> parameters = listOfOneStepShrinkables(5, 10);
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(parameters, originalError);
			PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.OFF);
			ShrunkFalsifiedSample sample = shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			assertThat(originalSample.equals(sample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ifNothingCanBeShrunkReturnOriginalValue() {
			List<Shrinkable<Object>> parameters = listOfOneStepShrinkables(10);
			Throwable originalError = failAndCatch("original error");
			FalsifiedSample originalSample = toFalsifiedSample(parameters, originalError);
			PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 9);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(originalSample.equals(sample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void ignoreShrinkingSuggestionsWithShrinkingDistanceAboveCurrentBest() {
			ShrinkToLarger shrinkableToLarger = new ShrinkToLarger(10);
			List<Shrinkable<Object>> parameters = asList(shrinkableToLarger.asGeneric());
			FalsifiedSample originalSample = toFalsifiedSample(parameters, null);
			PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = ignore -> TryExecutionResult.falsified(null);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(originalSample.equals(sample)).isTrue();
			assertThat(sample.countShrinkingSteps()).isEqualTo(0);

			verifyNoInteractions(falsifiedSampleReporter);
		}

		@Example
		void allowShrinkingWithSameDistance() {
			ShrinkWithFixedDistance shrinkableToLarger = new ShrinkWithFixedDistance(10);
			List<Shrinkable<Object>> parameters = asList(shrinkableToLarger.asGeneric());
			FalsifiedSample originalSample = toFalsifiedSample(parameters, null);
			PropertyShrinker shrinker = createShrinker(originalSample, ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = ignore -> TryExecutionResult.falsified(null);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(0));
			assertThat(sample.countShrinkingSteps()).isEqualTo(10);
		}
	}

	@Group
	class ShrinkSingleParameter {

		@Example
		void stepByStep() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 1);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(8);
		}

		@Example
		void inOneStep() {
			List<Shrinkable<Object>> shrinkables = listOfFullShrinkables(10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i <= 1);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(1);
		}

		@Example
		void stepByStepWithFilter() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> {
				Assume.that(i % 2 == 0);
				return i <= 1;
			});

			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isEqualTo(4);
		}

		@Example
		void withBoundedShrinkingBreakOffAfter1Second() {
			List<Shrinkable<Object>> shrinkables = asList(new ShrinkableTypesForTest.SlowShrinkable(20).asGeneric());
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.BOUNDED, 1);

			ShrunkFalsifiedSample sample = shrinker.shrink(alwaysFalsify());

			assertThat((int) sample.parameters().get(0)).isLessThan(20);
			assertThat((int) sample.parameters().get(0)).isGreaterThan(0);

			// TODO: Test that logging shrinking bound reached has happened
		}

		@Example
		void withUnboundedShrinkingDoNotBreakOff() {
			List<Shrinkable<Object>> shrinkables = asList(new ShrinkableTypesForTest.SlowShrinkable(10).asGeneric());
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			ShrunkFalsifiedSample sample = shrinker.shrink(alwaysFalsify());

			assertThat(sample.parameters()).isEqualTo(asList(0));
		}


	}

	@Group
	class SeveralParameters {
		@Example
		void shrinkAllParametersOneAfterTheOther() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer integer1, Integer integer2) -> {
				if (integer1 == 0) return true;
				return integer2 <= 1;
			});
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(1, 2));
			assertThat(sample.falsifyingError()).isNotPresent();
			assertThat(sample.countShrinkingSteps()).isGreaterThan(0);
			assertThat(createValues(sample)).containsExactly(1, 2);
		}

		@Example
		void goThroughShrinkingCycleAsOftenAsNeeded() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer i1, Integer i2) -> Math.abs(i2 - i1) > 1);
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(0, 0));
			assertThat(createValues(sample)).containsExactly(0, 0);
			assertThat(sample.countShrinkingSteps()).isGreaterThan(0);
		}

		@Example
		void falsifyingErrorComesFromActualShrunkSample() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, failAndCatch("original")), ShrinkingMode.FULL);

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
		void shrunkSampleParametersAreTheRealOnes() {
			List<Shrinkable<Object>> shrinkables = asList(
				new ShrinkableList<>(asList(new FullShrinkable(42)), 1, 5).asGeneric(),
				new ShrinkableList<>(asList(new FullShrinkable(17)), 0, 5).asGeneric()
			);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = paramFalsifier((List<Integer> list1, List<Integer> list2) -> {
				list1.add(101);
				list2.add(202);
				return false;
			});
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).containsExactly(
				asList(0, 101),
				asList(202)
			);
			assertThat(createValues(sample)).containsExactly(
				asList(0),
				asList()
			);
		}

		@Example
		void resultSampleConsistsOfActualUsedObjects_notOfValuesGeneratedByShrinkable() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = params -> {
				params.add(42);
				if (((int) params.get(0)) == 0) return true;
				if (((int) params.get(1)) <= 1) return true;
				return false;
			};
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);
			assertThat(sample.parameters()).isEqualTo(asList(1, 2, 42));
		}
	}

	@Group
	class FalsifiedSampleReporting {

		@Example
		void correctSampleIsReported() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(1);
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);
			shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			ArgumentCaptor<FalsifiedSample> sampleCaptor = ArgumentCaptor.forClass(FalsifiedSampleImpl.class);
			verify(falsifiedSampleReporter, times(1)).accept(sampleCaptor.capture());

			FalsifiedSample sample = sampleCaptor.getValue();
			assertThat(sample.parameters()).isEqualTo(asList(0));
		}

		@Example
		void allFalsifiedSamplesAreReported() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(5, 10);
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);
			shrinker.shrink(ignore -> TryExecutionResult.falsified(null));

			verify(falsifiedSampleReporter, times(15)).accept(any(FalsifiedSampleImpl.class));
		}

	}

	@Group
	class ErrorTypeDifferentiation {

		@Example
		void differentErrorTypeDoesNotCountAsSameError() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(50);

			AssertionError originalError = failAndCatch("original error");
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, originalError), ShrinkingMode.FULL);

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

		@Example
		void differentErrorStackTraceDoesNotCountAsSameError() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(50);
			AssertionError originalError = failAndCatch("original error");
			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, originalError), ShrinkingMode.FULL);

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
	}

	@Group
	class ShrinkingPairs {

		@Example
		void shrinkTwoParametersTogether() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer int1, Integer int2) -> {
				return int1 < 7 || int1.compareTo(int2) != 0;
			});
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(7, 7));
			assertThat(sample.countShrinkingSteps()).isGreaterThan(0);
			assertThat(createValues(sample)).containsExactly(7, 7);
		}

		@Property
		void shrinkAnyPairTogether(
			@ForAll @IntRange(min = 0, max = 3) int index1,
			@ForAll @IntRange(min = 0, max = 3) int index2
		) {
			Assume.that(index1 != index2);

			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10, 10, 10, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = params -> {
				int int1 = (int) params.get(index1);
				int int2 = (int) params.get(index2);
				return int1 < 7 || int1 != int2;
			};

			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);
			List<Object> expectedList = asList(0, 0, 0, 0);
			expectedList.set(index1, 7);
			expectedList.set(index2, 7);

			assertThat(sample.parameters()).isEqualTo(expectedList);
			assertThat(sample.countShrinkingSteps()).isGreaterThan(0);
		}

		@Disabled("Later optimization")
		// @Example
		void shrinkingPairSumShiftsTowardsRight() {
			List<Shrinkable<Object>> shrinkables = listOfOneStepShrinkables(10, 10);

			PropertyShrinker shrinker = createShrinker(toFalsifiedSample(shrinkables, null), ShrinkingMode.FULL);

			TestingFalsifier<List<Object>> falsifier = paramFalsifier((Integer int1, Integer int2) -> {
				return int1 + int2 < 20;
			});
			ShrunkFalsifiedSample sample = shrinker.shrink(falsifier);

			assertThat(sample.parameters()).isEqualTo(asList(0, 20));
			assertThat(sample.countShrinkingSteps()).isGreaterThan(0);
		}

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

	private List<Object> createValues(FalsifiedSample sample) {
		return sample.shrinkables().stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private List<Shrinkable<Object>> listOfOneStepShrinkables(int... args) {
		return Arrays.stream(args).mapToObj(i -> new OneStepShrinkable(i).asGeneric()).collect(Collectors.toList());
	}

	private List<Shrinkable<Object>> listOfFullShrinkables(int... args) {
		return Arrays.stream(args).mapToObj(i -> new FullShrinkable(i).asGeneric()).collect(Collectors.toList());
	}

	private PropertyShrinker createShrinker(FalsifiedSample originalSample, ShrinkingMode shrinkingMode) {
		return createShrinker(originalSample, shrinkingMode, 10);
	}

	private PropertyShrinker createShrinker(FalsifiedSample originalSample, ShrinkingMode shrinkingMode, int boundedShrinkingSeconds) {
		return new PropertyShrinker(
			originalSample,
			shrinkingMode,
			boundedShrinkingSeconds,
			falsifiedSampleReporter,
			null
		);
	}

	private FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(parameters, shrinkables, Optional.ofNullable(originalError));
	}

	@SuppressWarnings("unchecked")
	private <T> TestingFalsifier<List<Object>> paramFalsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	private <T1, T2> TestingFalsifier<List<Object>> paramFalsifier(BiPredicate<T1, T2> t1t2Falsifier) {
		return params -> {
			T1 t1 = (T1) params.get(0);
			T2 t2 = (T2) params.get(1);
			return t1t2Falsifier.test(t1, t2);
		};
	}

	private AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

}
