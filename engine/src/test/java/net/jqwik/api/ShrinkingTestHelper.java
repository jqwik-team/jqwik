package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.testing.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTestHelper {

	public static AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

	public static FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(parameters, shrinkables, Optional.ofNullable(originalError));
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

	public static <T> Falsifier<T> alwaysFalsify() {
		return ignore -> TryExecutionResult.falsified(null);
	}

	public static <T> TestingFalsifier<T> falsifier(Predicate<T> predicate) {
		return predicate::test;
	}

	public static <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<? extends T> arbitrary, Random random) {
		T value = falsifyThenShrink(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(expectedShrunkValue);
	}

}
