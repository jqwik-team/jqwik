package net.jqwik.properties;

import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

class ArbitraryTestHelper {

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			Shrinkable<T> value = generator.next(random);
			if (checker.apply(value.value()))
				return;
		}
		fail("Failed to generate at least one");
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			Shrinkable<T> value = generator.next(random);
			if (!checker.apply(value.value()))
				fail(String.format("Value [%s] failed to fulfill condition.", value.value().toString()));
		}
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Consumer<T> assertions) {
		Function<T, Boolean> checker = value -> {
			try {
				assertions.accept(value);
				return true;
			} catch (Throwable any) {
				return false;
			}
		};
		assertAllGenerated(generator, checker);
	}

	public static <T> Set<T> allShrunkValues(ShrinkCandidates<T> shrinker, T toShrink) {
		Set<T> shrinks = new HashSet<>();
		collectShrunkValuesWithMininumDistance(shrinker, toShrink, shrinks);
		return shrinks;
	}

	private static <T> void collectShrunkValuesWithMininumDistance(ShrinkCandidates<T> shrinker, T toShrink, Set<T> collector) {
		Set<T> shrinkCandidates = shrinker.nextCandidates(toShrink);
		int minDistance = minDistance(shrinkCandidates, shrinker);
		shrinkCandidates.stream()
			.filter(candidate -> shrinker.distance(candidate) == minDistance)
			.forEach(collector::add);
		shrinkCandidates.forEach(next -> collectShrunkValuesWithMininumDistance(shrinker, next, collector));
	}

	private static <T> int minDistance(Set<T> candidates, ShrinkCandidates<T> shrinker) {
		int minDistance = Integer.MAX_VALUE;
		for (T candidate : candidates) {
			int distance = shrinker.distance(candidate);
			if (distance < minDistance) minDistance = distance;
		}
		return minDistance;
	}


	@SafeVarargs
	public static <T> void assertGenerated(RandomGenerator<T> generator, T... expectedValues) {
		Random random = new Random();

		for (int i = 0; i < expectedValues.length; i++) {
			Shrinkable<T> actual = generator.next(random);
			T expected = expectedValues[i];
			if (!actual.value().equals(expected))
				fail(String.format("Generated value [%s] not equals to expected value [%s].", actual.toString(), expected.toString()));
		}
	}

	public static Shrinkable<List<Integer>> shrinkableListOfIntegers(int... numbers) {
		return new ContainerShrinkable<>(listOfShrinkableIntegers(numbers), ArrayList::new);
	}

	public static List<Shrinkable<Integer>> listOfShrinkableIntegers(int... numbers) {
		return Arrays.stream(numbers) //
					 .mapToObj(ArbitraryTestHelper::shrinkableInteger) //
					 .collect(Collectors.toList());
	}

	public static Shrinkable<Integer> shrinkableInteger(int anInt) {
		return new ShrinkableValue<>(anInt, new SimpleIntegerShrinker());
	}

	public static Shrinkable<String> shrinkableString(String aString) {
		return shrinkableString(aString.toCharArray());
	}

	public static Shrinkable<String> shrinkableString(char... chars) {
		return ContainerShrinkable.stringOf(listOfShrinkableChars(chars));
	}

	private static List<Shrinkable<Character>> listOfShrinkableChars(char[] chars) {
		List<Shrinkable<Character>> shrinkableChars = new ArrayList<>();
		for (char aChar : chars) {
			shrinkableChars.add(new ShrinkableValue<>(aChar, new SimpleCharacterShrinker()));
		}
		return shrinkableChars;
	}

	private static class SimpleCharacterShrinker implements ShrinkCandidates<Character> {

		@Override
		public Set<Character> nextCandidates(Character value) {
			if (value <= 'a')
				return Collections.emptySet();
			return Collections.singleton((char) (value - 1));
		}

		@Override
		public int distance(Character value) {
			return Math.abs(value - 'a');
		}
	}

	private static class SimpleIntegerShrinker implements ShrinkCandidates<Integer> {
		@Override
		public Set<Integer> nextCandidates(Integer value) {
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
