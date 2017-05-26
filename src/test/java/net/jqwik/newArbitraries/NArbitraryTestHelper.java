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

	public static <T> List<T> shrinkToEnd(NShrinkCandidates<T> shrinker, T toShrink) {
		ArrayList<T> shrinks = new ArrayList<>();
		collectShrinkResults(shrinker, toShrink, shrinks);
		return shrinks;
	}

	private static <T> void collectShrinkResults(NShrinkCandidates<T> shrinker, T toShrink, List<T> collector) {
		Set<T> shrink = shrinker.nextCandidates(toShrink);
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
		return new NContainerShrinkable<>(listOfShrinkableIntegers(numbers), ArrayList::new);
	}

	public static List<NShrinkable<Integer>> listOfShrinkableIntegers(int... numbers) {
		return Arrays.stream(numbers) //
					 .mapToObj(NArbitraryTestHelper::shrinkableInteger) //
					 .collect(Collectors.toList());
	}

	public static NShrinkable<Integer> shrinkableInteger(int anInt) {
		return new NShrinkableValue<>(anInt, new SimpleIntegerShrinker());
	}

	public static NShrinkable<String> shrinkableString(String aString) {
		return shrinkableString(aString.toCharArray());
	}

	public static NShrinkable<String> shrinkableString(char... chars) {
		return NContainerShrinkable.stringOf(listOfShrinkableChars(chars));
	}

	private static List<NShrinkable<Character>> listOfShrinkableChars(char[] chars) {
		List<NShrinkable<Character>> shrinkableChars = new ArrayList<>();
		for (char aChar : chars) {
			shrinkableChars.add(new NShrinkableValue<>(aChar, new SimpleCharacterShrinker()));
		}
		return shrinkableChars;
	}

	private static class SimpleCharacterShrinker implements NShrinkCandidates<Character> {

		@Override
		public Set<Character> nextCandidates(Character value) {
			if (value == 'a')
				return Collections.emptySet();
			return Collections.singleton((char) (value - 1));
		}

		@Override
		public int distance(Character value) {
			return value - 'a';
		}
	}

	private static class SimpleIntegerShrinker implements NShrinkCandidates<Integer> {
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
