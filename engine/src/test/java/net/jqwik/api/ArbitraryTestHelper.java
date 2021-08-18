package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.engine.*;
import net.jqwik.testing.*;

public class ArbitraryTestHelper {

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<? extends T> generator, Function<T, Boolean> checker) {
		Random random = SourceOfRandomness.current();
		TestingSupport.assertAtLeastOneGenerated(generator, random, checker, "Failed to generate at least one");
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

}
