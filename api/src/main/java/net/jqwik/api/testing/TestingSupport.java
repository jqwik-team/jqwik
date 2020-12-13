package net.jqwik.api.testing;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class TestingSupport {

	private TestingSupport() {
	}

	@API(status = INTERNAL)
	public static abstract class TestingSupportFacade {
		private static final TestingSupportFacade implementation;

		static {
			implementation = FacadeLoader.load(TestingSupportFacade.class);
		}

		protected abstract <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<T> falsifier);
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

	public static <T> Set<T> collectEdgeCases(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}

	public static <T> T shrinkToMinimal(Arbitrary<? extends T> arbitrary, Random random) {
		return shrinkToMinimal(arbitrary, random, ignore -> TryExecutionResult.falsified(null));
	}

	public static <T> T shrinkToMinimal(Arbitrary<? extends T> arbitrary, Random random, Falsifier<T> falsifier) {
		return TestingSupportFacade.implementation.falsifyThenShrink(arbitrary, random, falsifier);
	}

	private static void fail(String message) {
		throw new AssertionFailedError(message);
	}

}
