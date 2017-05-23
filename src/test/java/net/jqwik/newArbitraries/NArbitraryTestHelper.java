package net.jqwik.newArbitraries;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NArbitraryTestHelper {

	public static <T> void assertAtLeastOneGenerated(NShrinkableGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			NShrinkable<T> value = generator.next(random);
			if (checker.apply(value.value()))
				return;
		}
		fail("Failed to generate at least one");
	}

	public static <T> void assertAllGenerated(NShrinkableGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			NShrinkable<T> value = generator.next(random);
			if (!checker.apply(value.value()))
				fail(String.format("Value [%s] failed to fulfill condition.", value.toString()));
		}
	}

	public static <T> List<T> shrinkToEnd(NShrinker<T> shrinker, T toShrink) {
		ArrayList<T> shrinks = new ArrayList<>();
		collectShrinkResults(shrinker, toShrink, shrinks);
		return shrinks;
	}

	private static <T> void collectShrinkResults(NShrinker<T> shrinker, T toShrink, List<T> collector) {
		Set<T> shrink = shrinker.shrink(toShrink);
		collector.addAll(shrink);
		shrink.forEach(next -> collectShrinkResults(shrinker, next, collector));
	}

	public static <T> void assertGenerated(NShrinkableGenerator<T> generator, T... expectedValues) {
		Random random = new Random();

		for (int i = 0; i < expectedValues.length; i++) {
			NShrinkable<T> actual = generator.next(random);
			T expected = expectedValues[i];
			if (!actual.value().equals(expected))
				fail(String.format("Generated value [%s] not equals to expected value [%s].", actual.toString(), expected.toString()));
		}
	}

	public static NShrinkable<List<Integer>> shrinkableListOfIntegers(int... numbers) {
		return new NContainerShrinkable<>(listOfShrinkableIntegers(numbers), ArrayList::new, new NListShrinker<>());
	}

	public static List<NShrinkable<Integer>> listOfShrinkableIntegers(int... numbers) {
		return Arrays.stream(numbers) //
				.mapToObj(anInt -> new NShrinkableValue<>(anInt, new SimpleIntegerShrinker())) //
				.collect(Collectors.toList());
	}

	private static class SimpleIntegerShrinker implements NShrinker<Integer> {
		@Override
		public Set<Integer> shrink(Integer value) {
			if (value == 0)
				return Collections.emptySet();
			return Collections.singleton(value - 1);
		}

		@Override
		public int distance(Integer value) {
			return value;
		}
	}
}
