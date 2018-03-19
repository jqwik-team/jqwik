package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

public class ArbitraryTestHelper {

	@SafeVarargs
	public static <T> void assertAtLeastOneGeneratedOf(RandomGenerator<T> generator, T... values) {
		for (T value : values) {
			assertAtLeastOneGenerated(generator, value::equals, "Failed to generate " + value);
		}
	}

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker) {
		assertAtLeastOneGenerated(generator, checker, "Failed to generate at least one");
	}

	public static <T> Map<T, Integer> count(RandomGenerator<T> generator, int tries) {
		Random random = SourceOfRandomness.current();
		Map<T, Integer> counts = new HashMap<>();
		for (int i = 0; i < tries; i++) {
			Shrinkable<T> value = generator.next(random);
			T key = value.value();
			int previous = counts.computeIfAbsent(key, k -> 0);
			counts.put(key, previous + 1);
		}
		return counts;
	}

	public static <T> void assertAtLeastOneGenerated(RandomGenerator<T> generator, Function<T, Boolean> checker, String failureMessage) {
		Random random = SourceOfRandomness.current();
		for (int i = 0; i < 500; i++) {
			Shrinkable<T> value = generator.next(random);
			if (checker.apply(value.value()))
				return;
		}
		fail(failureMessage);
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Predicate<T> checker) {
		Random random = SourceOfRandomness.current();
		for (int i = 0; i < 100; i++) {
			Shrinkable<T> value = generator.next(random);
			if (!checker.test(value.value()))
				fail(String.format("Value [%s] failed to fulfill condition.", value.value().toString()));
		}
	}

	public static <T> void assertAllGenerated(RandomGenerator<T> generator, Consumer<T> assertions) {
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

	public static <T> Set<T> allShrunkValues(ShrinkCandidates<T> shrinker, T toShrink) {
		Set<T> shrinks = new HashSet<>();
		collectShrunkValuesWithMinimumDistance(shrinker, toShrink, shrinks);
		return shrinks;
	}

	private static <T> void collectShrunkValuesWithMinimumDistance(ShrinkCandidates<T> shrinker, T toShrink, Set<T> collector) {
		Set<T> shrinkCandidates = shrinker.nextCandidates(toShrink);
		int minDistance = minDistance(shrinkCandidates, shrinker);
		shrinkCandidates.stream()
			.filter(candidate -> shrinker.distance(candidate) == minDistance)
			.forEach(collector::add);
		shrinkCandidates.forEach(next -> collectShrunkValuesWithMinimumDistance(shrinker, next, collector));
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
		Random random = SourceOfRandomness.current();

		for (int i = 0; i < expectedValues.length; i++) {
			Shrinkable<T> actual = generator.next(random);
			T expected = expectedValues[i];
			if (!actual.value().equals(expected))
				fail(String.format("Generated value [%s] not equals to expected value [%s].", actual.toString(), expected.toString()));
		}
	}

	public static Shrinkable<List<Integer>> shrinkableListOfIntegers(int... numbers) {
		return new ContainerShrinkable<>(listOfShrinkableIntegers(numbers), ArrayList::new, 0);
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
		return ContainerShrinkable.stringOf(listOfShrinkableChars(chars), 0);
	}

	private static List<Shrinkable<Character>> listOfShrinkableChars(char[] chars) {
		List<Shrinkable<Character>> shrinkableChars = new ArrayList<>();
		for (char aChar : chars) {
			shrinkableChars.add(new ShrinkableValue<>(aChar, new SimpleCharacterShrinker()));
		}
		return shrinkableChars;
	}

	public static <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<T> arbitrary, Random random) {
		T value = shrinkToEnd(arbitrary, random);
		assertThat(value).isEqualTo(expectedShrunkValue);
	}

	public static <T> T shrinkToEnd(Arbitrary<T> arbitrary, Random random) {
		Shrinkable<T> shrinkable = arbitrary.generator(10).next(random);
		ShrinkResult<Shrinkable<T>> shrunk = new ValueShrinker<>(shrinkable).shrink(value -> false, null);
		return shrunk.shrunkValue().value();
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
