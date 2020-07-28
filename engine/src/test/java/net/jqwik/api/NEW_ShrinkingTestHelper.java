package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class NEW_ShrinkingTestHelper {

	public static AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

	public static FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSample(parameters, shrinkables, Optional.ofNullable(originalError));
	}

	@SuppressWarnings("unchecked")
	public static <T> TestingFalsifier<List<Object>> paramFalsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> TestingFalsifier<List<Object>> paramFalsifier(BiPredicate<T1, T2> t1t2Falsifier) {
		return params -> {
			T1 t1 = (T1) params.get(0);
			T2 t2 = (T2) params.get(1);
			return t1t2Falsifier.test(t1, t2);
		};
	}

	// public static <T> Falsifier<T> alwaysFalsify() {
	// 	return ignore -> TryExecutionResult.falsified(null);
	// }
	//
	// public static <T> TestingFalsifier<T> falsifier(Predicate<T> predicate) {
	// 	return predicate::test;
	// }

	@SuppressWarnings("unchecked")
	public static <T> Falsifier<List<Object>> toParmaFalsifier(Falsifier<T> tFalsifier) {
		return params -> {
			T t = (T) params.get(0);
			return tFalsifier.execute(t);
		};
	}

	// public static <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<? extends T> arbitrary, Random random) {
	// 	T value = shrinkToEnd(arbitrary, random);
	// 	assertThat(value).isEqualTo(expectedShrunkValue);
	// }

	public static <T> T shrinkToEnd(Arbitrary<? extends T> arbitrary, Random random) {
		return falsifyThenShrink(arbitrary, random, ignore -> TryExecutionResult.falsified(null));
	}

	@SuppressWarnings("unchecked")
	public static <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<T> falsifier) {
		RandomGenerator<? extends T> generator = arbitrary.generator(10);
		Throwable[] originalError = new Throwable[1];
		Shrinkable<T> falsifiedShrinkable =
			(Shrinkable<T>) ArbitraryTestHelper.generateUntil(generator, random, value -> {
				TryExecutionResult result = falsifier.execute(value);
				if (result.isFalsified()) {
					originalError[0] = result.throwable().orElse(null);
				}
				return result.isFalsified();
			});
		return shrinkToEnd(falsifiedShrinkable, falsifier, originalError[0]);
	}

	@SuppressWarnings("unchecked")
	public static <T> T shrinkToEnd(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<T> falsifier,
		Throwable originalError
	) {
		ShrunkFalsifiedSample sample = shrink(falsifiedShrinkable, falsifier, originalError);
		return (T) sample.parameters().get(0);
	}

	public static <T> ShrunkFalsifiedSample shrink(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<T> falsifier,
		Throwable originalError
	) {
		FalsifiedSample sample = toFalsifiedSample(falsifiedShrinkable, originalError);
		Consumer<FalsifiedSample> parametersReporter = ignore -> {};
		NEW_PropertyShrinker shrinker = new NEW_PropertyShrinker(sample, ShrinkingMode.FULL, parametersReporter, null);

		return shrinker.shrink(toParmaFalsifier(falsifier));
	}

	@SuppressWarnings("unchecked")
	public static <T> FalsifiedSample toFalsifiedSample(Shrinkable<T> falsifiedShrinkable, Throwable originalError) {
		List<Shrinkable<Object>> shrinkables = Collections.singletonList((Shrinkable<Object>) falsifiedShrinkable);
		return toFalsifiedSample(shrinkables, originalError);
	}

}
