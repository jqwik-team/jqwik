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

	private List<Shrinkable<Object>> listOfShrinkableInts(int... args) {
		Range<BigInteger> range = Range.of(BigInteger.ZERO, BigInteger.valueOf(1000));
		return Arrays.stream(args).mapToObj(i -> {
			BigInteger value = BigInteger.valueOf(i);
			return new ShrinkableBigInteger(value, range, BigInteger.ZERO)
				.map(BigInteger::intValueExact)
				.asGeneric();
		}).collect(Collectors.toList());
	}

	private FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(parameters, shrinkables, Optional.ofNullable(originalError), Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	private <T> TestingFalsifier<List<Object>> paramFalsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	@Property(tries = 10)
	void singleParameter(
		@ForAll @IntRange(min = 2, max = 1000) int initialValue,
		@ForAll @IntRange(min = 1, max = 1000) int shrinkingResult
	) {
		Assume.that(initialValue > shrinkingResult);

		List<Shrinkable<Object>> shrinkables = listOfShrinkableInts(initialValue);
		FalsifiedSample originalSample = toFalsifiedSample(shrinkables, null);
		PropertyShrinker shrinker = new PropertyShrinker(
			originalSample,
			ShrinkingMode.FULL,
			10,
			Mockito.mock(Consumer.class),
			null
		);

		Falsifier<List<Object>> falsifier = paramFalsifier((Integer i) -> i < shrinkingResult);
		ShrunkFalsifiedSample shrunkSample = shrinker.shrink(falsifier);

		assertThat(shrunkSample.parameters()).isEqualTo(asList(shrinkingResult));
		assertThat(shrinker.shrinkingSequence()).hasSizeGreaterThan(0);

		ShrunkSampleRecreator recreator = new ShrunkSampleRecreator(originalSample);
		ShrunkFalsifiedSample recreatedSample = recreator.recreateFrom(shrinker.shrinkingSequence());
		assertThat(recreatedSample).isEqualTo(shrunkSample);
	}

}
