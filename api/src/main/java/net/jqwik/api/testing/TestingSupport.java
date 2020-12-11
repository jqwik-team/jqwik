package net.jqwik.api.testing;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class TestingSupport {

	private TestingSupport() {
	}

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Random random, Predicate<T> checker) {
		Optional<? extends Shrinkable<? extends T>> failure =
				generator
						.stream(random)
						.limit(100)
						.filter(shrinkable -> !checker.test(shrinkable.value()))
						.findAny();

		failure.ifPresent(shrinkable -> {
			fail(String.format("Value [%s] failed to fulfill condition.", shrinkable.value().toString()));
		});
	}

	private static void fail(String message) {
		throw new AssertionFailedError(message);
	}

}
