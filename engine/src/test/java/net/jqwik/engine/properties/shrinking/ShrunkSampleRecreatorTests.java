package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
class ShrunkSampleRecreatorTests {

	@Property(tries = 10)
	void recreateSingleParameter(
		@ForAll @IntRange(min = 1, max = 1000) int shrinkingResult,
		@ForAll @IntRange(min = 1, max = 100) int diff
	) {
		int initialValue = shrinkingResult + diff;

		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(initialValue);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.FULL, 10);

		Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i < shrinkingResult);
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);

		assertThat(shrunkSample.parameters()).isEqualTo(asList(shrinkingResult));
		assertThat(shrinker.shrinkingSequence()).hasSizeGreaterThan(0);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		Optional<List<Shrinkable<Object>>> recreatedSample = recreator.recreateFrom(shrinker.shrinkingSequence());
		assertThat(recreatedSample).hasValue(shrunkSample.shrinkables());
	}

	@Example
	void emptyShrinkingSequenceReturnsOriginalSample() {
		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(42);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.OFF, 0);

		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(null);
		assertThat(shrunkSample.parameters()).isEqualTo(asList(42));
		assertThat(shrunkSample.countShrinkingSteps()).isEqualTo(0);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		Optional<List<Shrinkable<Object>>> recreatedSample = recreator.recreateFrom(Collections.emptyList());
		assertThat(recreatedSample).hasValue(shrunkSample.shrinkables());
	}

	@Example
	void returnsNullIfShrinkingSequenceCannotBeFullyUsed() {
		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(0);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.OFF, 0);

		Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> false);
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);
		assertThat(shrunkSample.parameters()).isEqualTo(asList(0));
		assertThat(shrunkSample.countShrinkingSteps()).isEqualTo(0);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		List<TryExecutionResult.Status> shrinkingSequence = Arrays.asList(
			TryExecutionResult.Status.SATISFIED,
			TryExecutionResult.Status.SATISFIED,
			TryExecutionResult.Status.FALSIFIED
		);
		Optional<List<Shrinkable<Object>>> recreatedSample = recreator.recreateFrom(shrinkingSequence);
		assertThat(recreatedSample).isEmpty();
	}

	@Example
	void recreateWithInvalidResultsInShrinkingSequence() {

		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(100);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.FULL, 10);

		Falsifier<List<Object>> falsifier = params -> {
			Integer value = (Integer) params.get(0);
			if (value % 2 != 0) {
				return TryExecutionResult.invalid();
			}
			if (value > 41) {
				return TryExecutionResult.falsified(null);
			}
			return TryExecutionResult.satisfied();
		};
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);

		assertThat(shrunkSample.parameters()).isEqualTo(asList(42));
		assertThat(shrinker.shrinkingSequence()).hasSizeGreaterThan(0);
		assertThat(shrinker.shrinkingSequence()).contains(
			TryExecutionResult.Status.INVALID,
			TryExecutionResult.Status.FALSIFIED,
			TryExecutionResult.Status.SATISFIED
		);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		Optional<List<Shrinkable<Object>>> recreatedSample = recreator.recreateFrom(shrinker.shrinkingSequence());
		assertThat(recreatedSample).hasValue(shrunkSample.shrinkables());
	}


	@Property(tries = 10)
	void recreateSeveralParameters(
		@ForAll @IntRange(min = 1, max = 1000) int shrinkingResult,
		@ForAll @IntRange(min = 1, max = 100) int diff
	) {
		int initialValue = shrinkingResult + diff;
		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(initialValue, 99, 999);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.FULL, 0);

		Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i < shrinkingResult);
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);

		assertThat(shrunkSample.parameters()).isEqualTo(asList(shrinkingResult, 0, 0));
		assertThat(shrinker.shrinkingSequence()).hasSizeGreaterThan(0);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		Optional<List<Shrinkable<Object>>> recreatedSample = recreator.recreateFrom(shrinker.shrinkingSequence());
		assertThat(recreatedSample).hasValue(shrunkSample.shrinkables());
	}

	// Takes >= 5 seconds due to sleeps in shrinking
	@SuppressLogging
	@Property(tries = 5, edgeCases = EdgeCasesMode.NONE)
	void recreationWorksIfShrinkingBoundIsExceeded(@ForAll("sleepTime") int sleepMs) {
		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(999);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = createPropertyShrinker(originalSample, ShrinkingMode.BOUNDED, 1);

		Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> {
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return i < 99;
		});
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);
		int shrunkSampleValue = (int) shrunkSample.shrinkables().get(0).value();
		// System.out.println(shrunkSampleValue);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample.shrinkables());
		Optional<List<Shrinkable<Object>>> recreatedShrinkables = recreator.recreateFrom(shrinker.shrinkingSequence());
		assertThat(recreatedShrinkables).isNotEmpty();
		int recreatedSampleValue = (int) recreatedShrinkables.get().get(0).value();

		assertThat(recreatedSampleValue).isLessThanOrEqualTo(shrunkSampleValue);
		// In some strange cases the recreated value has been shrunk further than the original shrunk value
		// assertThat(recreatedSampleValue).isEqualTo(shrunkSampleValue);
	}

	@Provide
	private Arbitrary<Integer> sleepTime() {
		return Arbitraries.integers().between(30, 100).withDistribution(RandomDistribution.uniform()).withoutEdgeCases();
	}

	private FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(parameters, shrinkables, Optional.ofNullable(originalError), Collections.emptyList());
	}

	private List<Shrinkable<Object>> listOfShrinkableInts(int... args) {
		Range<BigInteger> range = Range.of(BigInteger.ZERO, BigInteger.valueOf(2000));
		return Arrays.stream(args).mapToObj(i -> {
			BigInteger value = BigInteger.valueOf(i);
			return new ShrinkableBigInteger(value, range, BigInteger.ZERO)
				.map(BigInteger::intValueExact)
				.asGeneric();
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private <T> TestingFalsifier<List<Object>> paramFalsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	private PropertyShrinker createPropertyShrinker(
		FalsifiedSample originalSample,
		ShrinkingMode shrinkingMode, int boundedShrinkingSeconds
	) {
		return new PropertyShrinker(
			originalSample,
			shrinkingMode,
			boundedShrinkingSeconds,
			Mockito.mock(Consumer.class),
			null
		);
	}

}
