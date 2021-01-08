package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.engine.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

public class ArbitraryTestHelper {

	@SafeVarargs
	public static <T> void assertAtLeastOneGeneratedOf(RandomGenerator<? extends T> generator, T... values) {
		for (T value : values) {
			assertAtLeastOneGenerated(generator, value::equals, "Failed to generate " + value);
		}
	}

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<? extends T> generator, Function<T, Boolean> checker) {
		assertAtLeastOneGenerated(generator, checker, "Failed to generate at least one");
	}

	public static <T> void assertAtLeastOneGenerated(
			RandomGenerator<? extends T> generator,
			Function<T, Boolean> checker,
			String failureMessage
	) {
		Random random = SourceOfRandomness.current();
		TestingSupport.assertAtLeastOneGenerated(generator, random, checker, failureMessage);
	}

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Predicate<T> checker) {
		TestingSupport.assertAllGenerated(generator, SourceOfRandomness.current(), checker);
	}

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Consumer<T> assertions) {
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
	static <T> void assertGeneratedExactly(RandomGenerator<? extends T> generator, T... expectedValues) {
		Random random = SourceOfRandomness.current();

		List<T> generated = generator
								.stream(random)
								.limit(expectedValues.length)
								.map(Shrinkable::value)
								.collect(Collectors.toList());

		assertThat(generated).containsExactly(expectedValues);
	}

}
