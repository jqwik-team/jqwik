package net.jqwik.testing;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;
import static org.assertj.core.api.Assertions.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class TestingSupport {

	private TestingSupport() {
	}

	public static <T extends @Nullable Object> void checkAllGenerated(Arbitrary<T> arbitrary, Random random, Predicate<? super T> checker) {
		checkAllGenerated(arbitrary.generator(1000), random, checker);
	}

	public static <T extends @Nullable Object> void checkAllGenerated(RandomGenerator<T> generator, Random random, Predicate<? super T> checker) {
		Optional<Shrinkable<T>> failure =
			generator
				.stream(random)
				.limit(100)
				.filter(shrinkable -> !checker.test(shrinkable.value()))
				.findAny();

		failure.ifPresent(shrinkable -> {
			Assertions.fail(String.format("Value [%s] failed to fulfill condition.", shrinkable.value()));
		});
	}

	public static <T extends @Nullable Object> void assertAllGenerated(Arbitrary<T> arbitrary, Random random, Consumer<? super T> assertions) {
		assertAllGenerated(arbitrary.generator(1000), random, assertions);
	}

	public static <T extends @Nullable Object> void assertAllGenerated(RandomGenerator<T> generator, Random random, Consumer<? super T> assertions) {
		Predicate<T> checker = value -> {
			assertions.accept(value);
			return true;
		};
		checkAllGenerated(generator, random, checker);
	}

	public static <T extends @Nullable Object> void assertAllGeneratedEqualTo(RandomGenerator<T> generator, Random random, T expected) {
		assertAllGenerated(
			generator,
			random,
			value -> assertThat(value).isEqualTo(expected)
		);
	}

	public static <T extends @Nullable Object> void assertAllGeneratedEqualTo(Arbitrary<T> arbitrary, Random random, T expected) {
		assertAllGeneratedEqualTo(
			arbitrary.generator(1000),
			random,
			expected
		);
	}

	public static <T extends @Nullable Object> void checkAtLeastOneGenerated(
		RandomGenerator<T> generator,
		Random random,
		Predicate<? super T> checker,
		String failureMessage
	) {
		Optional<Shrinkable<T>> success =
			generator
				.stream(random)
				.limit(5000)
				.filter(shrinkable -> checker.test(shrinkable.value()))
				.findAny();
		if (!success.isPresent()) {
			fail(failureMessage);
		}
	}

	public static <T extends @Nullable Object> void checkAtLeastOneGenerated(
		RandomGenerator<T> generator,
		Random random,
		Predicate<? super T> checker
	) {
		checkAtLeastOneGenerated(generator, random, checker, "Failed to generate at least one");
	}

	public static <T extends @Nullable Object> void checkAtLeastOneGenerated(
		Arbitrary<? extends T> arbitrary,
		Random random,
		Predicate<T> checker
	) {
		checkAtLeastOneGenerated(arbitrary.generator(1000), random, checker);
	}

	@SafeVarargs
	public static <T extends @Nullable Object> void assertAtLeastOneGeneratedOf(
		RandomGenerator<? extends T> generator,
		Random random,
		T... values
	) {
		for (T value : values) {
			checkAtLeastOneGenerated(generator, random, x -> Objects.equals(x, value), "Failed to generate " + value);
		}
	}

	@SafeVarargs
	public static <T extends @Nullable Object> void assertGeneratedExactly(RandomGenerator<? extends T> generator, Random random, T... expectedValues) {
		List<T> generated = generator
			.stream(random)
			.limit(expectedValues.length)
			.map(Shrinkable::value)
			.collect(Collectors.toList());

		assertThat(generated).containsExactly(expectedValues);
	}

	public static <T extends @Nullable Object> Set<Shrinkable<T>> collectEdgeCaseShrinkables(EdgeCases<T> edgeCases) {
		Set<Shrinkable<T>> shrinkables = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			shrinkables.add(edgeCase);
		}
		return shrinkables;
	}

	public static <T extends @Nullable Object> Set<T> collectEdgeCaseValues(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}

	public static <T extends @Nullable Object> T generateFirst(Arbitrary<T> arbitrary, Random random) {
		RandomGenerator<T> generator = arbitrary.generator(1, true);
		return generator.next(random).value();
	}

	public static <T extends @Nullable Object> Map<T, Long> count(RandomGenerator<T> generator, int tries, Random random) {
		return generator
			.stream(random)
			.limit(tries)
			.map(Shrinkable::value)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	public static <T extends @Nullable Object> Shrinkable<T> generateUntil(RandomGenerator<T> generator, Random random, Function<? super T, Boolean> condition) {
		return TestingSupportFacade.implementation.generateUntil(generator, random, condition);
	}

	@API(status = EXPERIMENTAL, since = "1.6.0")
	public static String singleLineReport(Object any) {
		return TestingSupportFacade.implementation.singleLineReport(any);
	}

	@API(status = EXPERIMENTAL, since = "1.6.0")
	public static List<String> multiLineReport(Object any) {
		return TestingSupportFacade.implementation.multiLineReport(any);
	}
}