package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class RandomGenerators {

	public static <U> RandomGenerator<U> choose(List<U> values) {
		if (values.size() == 0) {
			return fail("empty set of values");
		} else {
			return choose(values.size()).map(values::get);
		}
	}

	public static RandomGenerator<Integer> choose(int upperSizeExcluded) {
		if (upperSizeExcluded == 0) {
			return fail("empty set of values");
		} else {
			return random -> {
				int value = random.nextInt(upperSizeExcluded);
				return new ShrinkableValue<>(value, new SizeShrinkCandidates());
			};
		}

	}

	public static <U> RandomGenerator<U> choose(U[] values) {
		return choose(Arrays.asList(values));
	}

	public static <T extends Enum<T>> RandomGenerator<T> choose(Class<T> enumClass) {
		return choose(enumClass.getEnumConstants());
	}

	public static RandomGenerator<Character> choose(char[] characters) {
		Character[] validCharacters = new Character[characters.length];
		for (int i = 0; i < characters.length; i++) {
			validCharacters[i] = characters[i];
		}
		return choose(validCharacters);
	}

	public static RandomGenerator<Character> chars(char min, char max) {
		return integers(min, max).map(anInt -> ((char) (int) anInt));
	}

	public static RandomGenerator<Byte> bytes(byte min, byte max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::byteValueExact);
	}

	public static RandomGenerator<Short> shorts(short min, short max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::shortValueExact);
	}

	public static RandomGenerator<Integer> integers(int min, int max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::intValueExact);
	}

	public static RandomGenerator<Long> longs(long min, long max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::longValueExact);
	}

	public static RandomGenerator<BigInteger> bigIntegers(
		BigInteger min, BigInteger max, BigInteger... partitionPoints
	) {
		return RandomIntegralGenerators.bigIntegers(min, max, partitionPoints);
	}

	public static RandomGenerator<Double> doubles(double min, double max, int scale) {
		return bigDecimals(BigDecimal.valueOf(min), BigDecimal.valueOf(max), scale).map(BigDecimal::doubleValue);
	}

	public static RandomGenerator<Float> floats(float min, float max, int scale) {
		return bigDecimals(BigDecimal.valueOf((double) min), BigDecimal.valueOf((double) max), scale).map(BigDecimal::floatValue);
	}

	public static RandomGenerator<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale, BigDecimal... partitionPoints) {
		return RandomDecimalGenerators.bigDecimals(min, max, scale, partitionPoints);
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		return container(elementGenerator, ArrayList::new, minSize, maxSize);
	}

	public static RandomGenerator<String> strings(
		RandomGenerator<Character> elementGenerator, int minLength, int maxLength
	) {
		return container(elementGenerator, ContainerShrinkable.CREATE_STRING, minLength, maxLength);
	}

	private static <T, C> RandomGenerator<C> container( //
			RandomGenerator<T> elementGenerator, //
			Function<List<T>, C> containerFunction, //
			int minSize, int maxSize) {
		RandomGenerator<Integer> lengthGenerator = integers(minSize, maxSize);
		return random -> {
			int listSize = lengthGenerator.next(random).value();
			List<Shrinkable<T>> list = new ArrayList<>();
			while (list.size() < listSize) {
				list.add(elementGenerator.next(random));
			}
			return new ContainerShrinkable<>(list, containerFunction, minSize);
		};
	}

	// TODO: Get rid of duplication with container(...)
	public static <T> RandomGenerator<Set<T>> set(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		RandomGenerator<Integer> lengthGenerator = integers(minSize, maxSize);
		return random -> {
			int listSize = lengthGenerator.next(random).value();
			List<Shrinkable<T>> list = new ArrayList<>();
			Set<T> elements = new HashSet<>();
			while (list.size() < listSize) {
				Shrinkable<T> next = elementGenerator.next(random);
				if (elements.contains(next.value()))
					continue;
				list.add(next);
				elements.add(next.value());
			}
			return new ContainerShrinkable<>(list, HashSet::new, minSize);
		};
	}

	public static <T> RandomGenerator<T> samples(List<Shrinkable<T>> samples) {
		AtomicInteger tryCount = new AtomicInteger(0);
		return ignored -> {
			if (tryCount.get() >= samples.size())
				tryCount.set(0);
			return samples.get(tryCount.getAndIncrement());
		};
	}

	@SafeVarargs
	public static <T> RandomGenerator<T> samples(Shrinkable<T>... samples) {
		return samples(Arrays.asList(samples));
	}

	public static <T> RandomGenerator<T> frequency(Tuples.Tuple2<Integer, T>[] frequencies) {
		FrequencyGenerator<T> frequencyGenerator = new FrequencyGenerator<>(frequencies);
		return frequencyGenerator;
	}


	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new JqwikException(message);
		};
	}
}
