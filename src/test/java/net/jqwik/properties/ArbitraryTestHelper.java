package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;

public class ArbitraryTestHelper {

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			T value = generator.next(random);
			if (checker.apply(value))
				return;
		}
		fail("Failed to generate at least one");
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			T value = generator.next(random);
			if (!checker.apply(value))
				fail(String.format("Value [%s] failed to fulfill condition.", value.toString()));
		}
	}

	public static <T> void assertGenerated(RandomGenerator<T> generator, T... expectedValues) {
		Random random = new Random();

		for (int i = 0; i < expectedValues.length; i++) {
			T actual = generator.next(random);
			T expected = expectedValues[i];
			if (!actual.equals(expected))
				fail(String.format("Generated value [%s] not equals to expected value [%s].", actual.toString(), expected.toString()));
		}
	}

}
