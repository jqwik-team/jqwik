package net.jqwik.testing;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.assertj.core.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;
import static org.assertj.core.api.Assertions.*;

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
			Assertions.fail(String.format("Value [%s] failed to fulfill condition.", shrinkable.value()));
		});
	}

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Random random, Consumer<T> assertions) {
		Predicate<T> checker = value -> {
			try {
				assertions.accept(value);
				return true;
			} catch (Throwable any) {
				return false;
			}
		};
		assertAllGenerated(generator, random, checker);
	}

	public static <T> void assertAtLeastOneGenerated(
			RandomGenerator<? extends T> generator,
			Random random,
			Function<T, Boolean> checker,
			String failureMessage
	) {
		Optional<? extends Shrinkable<? extends T>> success =
				generator
						.stream(random)
						.limit(5000)
						.filter(shrinkable -> checker.apply(shrinkable.value()))
						.findAny();
		if (!success.isPresent()) {
			fail(failureMessage);
		}
	}

	public static <T> void assertAtLeastOneGenerated(
			RandomGenerator<? extends T> generator,
			Random random,
			Function<T, Boolean> checker
	) {
		assertAtLeastOneGenerated(generator, random, checker, "Failed to generate at least one");
	}

	@SafeVarargs
	public static <T> void assertAtLeastOneGeneratedOf(
		RandomGenerator<? extends T> generator,
		Random random,
		T... values
	) {
		for (T value : values) {
			assertAtLeastOneGenerated(generator, random, value::equals, "Failed to generate " + value);
		}
	}

	@SafeVarargs
	public static <T> void assertGeneratedExactly(RandomGenerator<? extends T> generator, Random random, T... expectedValues) {
		List<T> generated = generator
			.stream(random)
			.limit(expectedValues.length)
			.map(Shrinkable::value)
			.collect(Collectors.toList());

		assertThat(generated).containsExactly(expectedValues);
	}


	public static <T> Set<Shrinkable<T>> collectEdgeCaseShrinkables(EdgeCases<T> edgeCases) {
		Set<Shrinkable<T>> shrinkables = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			shrinkables.add(edgeCase);
		}
		return shrinkables;
	}

	public static <T> Set<T> collectEdgeCaseValues(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}

	public static <T> T generateFirst(Arbitrary<T> arbitrary, Random random) {
		RandomGenerator<T> generator = arbitrary.generator(1, true);
		return generator.next(random).value();
	}

	public static <T> Map<T, Long> count(RandomGenerator<T> generator, int tries, Random random) {
		return generator
					   .stream(random)
					   .limit(tries)
					   .map(shrinkable -> shrinkable.value())
					   .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	// TODO: Call from TestingSupportFacade
	public static <T> Shrinkable<T> generateUntil(RandomGenerator<T> generator, Random random, Function<T, Boolean> condition) {
		long maxTries = 1000;
		return generator
					   .stream(random)
					   .limit(maxTries)
					   .filter(shrinkable -> condition.apply(shrinkable.value()))
					   .findFirst()
					   .orElseThrow(() -> new JqwikException("Failed to generate value that fits condition after " + maxTries + " tries."));
	}


}
