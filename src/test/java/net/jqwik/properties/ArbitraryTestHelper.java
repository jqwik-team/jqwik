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

	public static <T> List<T> shrinkToEnd(ShrinkCandidates<T> shrinker, T toShrink) {
		ArrayList<T> shrinks = new ArrayList<>();
		collectShrinkResults(shrinker, toShrink, shrinks);
		return shrinks;
	}

	private static <T> void collectShrinkResults(ShrinkCandidates<T> shrinker, T toShrink, List<T> collector) {
		Set<T> shrink = shrinker.nextCandidates(toShrink);
		collector.addAll(shrink);
		shrink.forEach(next -> collectShrinkResults(shrinker, next, collector));
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
