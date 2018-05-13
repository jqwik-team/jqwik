package net.jqwik.properties;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public class ArbitraryTestHelper {

	@SafeVarargs
	public static <T> void assertAtLeastOneGeneratedOf(RandomGenerator<T> generator, T... values) {
		for (T value : values) {
			assertAtLeastOneGenerated(generator, value::equals, "Failed to generate " + value);
		}
	}

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		assertAtLeastOneGenerated(generator, checker, "Failed to generate at least one");
	}

	public static <T> Map<T, Integer> count(RandomGenerator<T> generator, int tries) {
		Random random = SourceOfRandomness.current();
		Map<T, Integer> counts = new HashMap<>();
		for (int i = 0; i < tries; i++) {
			Shrinkable<T> value = generator.next(random);
			T key = value.value();
			int previous = counts.computeIfAbsent(key, k -> 0);
			counts.put(key, previous + 1);
		}
		return counts;
	}

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker, String failureMessage) {
		Random random = SourceOfRandomness.current();
		for (int i = 0; i < 500; i++) {
			Shrinkable<T> value = generator.next(random);
			if (checker.apply(value.value()))
				return;
		}
		fail(failureMessage);
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Predicate<T> checker) {
		Random random = SourceOfRandomness.current();
		for (int i = 0; i < 100; i++) {
			Shrinkable<T> value = generator.next(random);
			if (!checker.test(value.value()))
				fail(String.format("Value [%s] failed to fulfill condition.", value.value().toString()));
		}
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Consumer<T> assertions) {
		Predicate<T> checker = value -> {
			try {
				assertions.accept(value);
				return true;
			} catch (Throwable any) {
				return false;
			}
		};
		assertAllGenerated(generator, checker);
	}

	@SafeVarargs
	public static <T> void assertGenerated(RandomGenerator<T> generator, T... expectedValues) {
		Random random = SourceOfRandomness.current();

		for (T expectedValue : expectedValues) {
			Shrinkable<T> actual = generator.next(random);
			T expected = expectedValue;
			if (!actual.value().equals(expected))
				fail(String.format("Generated value [%s] not equals to expected value [%s].", actual.toString(), expected
					.toString()));
		}
	}

	public static <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<T> arbitrary, Random random) {
		T value = shrinkToEnd(arbitrary, random);
		assertThat(value).isEqualTo(expectedShrunkValue);
	}

	public static <T> T shrinkToEnd(Arbitrary<T> arbitrary, Random random) {
		Shrinkable<T> shrinkable = arbitrary.generator(10).next(random);
		ShrinkingSequence<T> sequence = shrinkable.shrink(value -> false);
		while(sequence.next(() -> {}, ignore -> {}));
		return sequence.current().value();
	}

}
