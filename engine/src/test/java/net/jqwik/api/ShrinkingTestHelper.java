package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

import static org.assertj.core.api.Assertions.*;

public class ShrinkingTestHelper {

	public static final Consumer<List<Object>> falsifiedReporterStub = ignore -> {};

	public static final Reporter reporterStub = Mockito.mock(Reporter.class);

	@SuppressWarnings("unchecked")
	public static <T> List<Shrinkable<Object>> toListOfShrinkables(Shrinkable<T>... shrinkables) {
		ArrayList<Shrinkable<Object>> parameterList = new ArrayList<>();
		for (Shrinkable<T> shrinkable : shrinkables) {
			parameterList.add((Shrinkable<Object>) shrinkable);
		}
		return parameterList;
	}

	@SuppressWarnings("unchecked")
	public static <T> Falsifier<List<Object>> parameterFalsifier(Falsifier<T> tFalsifier) {
		return params -> {
			T t = (T) params.get(0);
			return tFalsifier.execute(t);
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> TestingFalsifier<List<Object>> falsifier(Predicate<T> tFalsifier) {
		return params -> {
			T seq = (T) params.get(0);
			return tFalsifier.test(seq);
		};
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> TestingFalsifier<List<Object>> falsifier(BiPredicate<T1, T2> t1t2Falsifier) {
		return params -> {
			T1 t1 = (T1) params.get(0);
			T2 t2 = (T2) params.get(1);
			return t1t2Falsifier.test(t1, t2);
		};
	}

	public static AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

	public static <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<? extends T> arbitrary, Random random) {
		T value = shrinkToEnd(arbitrary, random);
		assertThat(value).isEqualTo(expectedShrunkValue);
	}

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

	public static <T> T shrinkToEnd(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<T> falsifier,
		Throwable originalError
	) {
		return shrinkToEnd(falsifiedShrinkable, falsifier, t -> {}, originalError);
	}

	@SuppressWarnings("unchecked")
	public static <T> T shrinkToEnd(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<T> falsifier,
		Consumer<T> falsifiedReporter,
		Throwable originalError
	) {
		PropertyShrinkingResult result = shrink(falsifiedShrinkable, falsifier, falsifiedReporter, originalError);
		return (T) result.sample().get(0);
	}

	@SuppressWarnings("unchecked")
	public static <T> PropertyShrinkingResult shrink(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<T> falsifier,
		Consumer<T> falsifiedReporter,
		Throwable originalError
	) {
		List<Shrinkable<Object>> parameters = toListOfShrinkables(falsifiedShrinkable);
		Consumer<List<Object>> parametersReporter = params -> falsifiedReporter.accept((T) params.get(0));
		PropertyShrinker shrinker = new PropertyShrinker(parameters, ShrinkingMode.FULL, reporterStub, parametersReporter);

		return shrinker.shrink(parameterFalsifier(falsifier), originalError);
	}
}
