package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class ArbitrariesTests {

	enum MyEnum {
		Yes,
		No,
		Maybe
	}

	private Random random = new Random();

	@Example
	void randomValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.randomValue(random -> Integer.toString(random.nextInt(10)));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Integer.parseInt(value) < 10);
	}

	@Example
	void fromGenerator() {
		Arbitrary<String> stringArbitrary = Arbitraries
				.fromGenerator(random -> Shrinkable.unshrinkable(Integer.toString(random.nextInt(10))));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Integer.parseInt(value) < 10);
	}

	@Example
	void ofValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, (String value) -> Arrays.asList("1", "hallo", "test").contains(value));
	}

	@Example
	void ofEnum() {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);
		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, (MyEnum value) -> Arrays.asList(MyEnum.class.getEnumConstants()).contains(value));
	}

	@Example
	void string() {
		Arbitrary<String> stringArbitrary = Arbitraries.string('a', 'd', 0, 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator, 0, 5);
	}

	@Example
	void stringFromCharset() {
		char[] validChars = new char[] { 'a', 'b', 'c', 'd' };
		Arbitrary<String> stringArbitrary = Arbitraries.string(validChars, 2, 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator, 2, 5);
	}

	private void assertGeneratedString(RandomGenerator<String> generator, int minLength, int maxLength) {
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value.length() >= minLength && value.length() <= maxLength);
		List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
		ArbitraryTestHelper.assertAllGenerated(generator,
				(String value) -> value.chars().allMatch(i -> allowedChars.contains(Character.valueOf((char) i))));
	}

	@Example
	void samplesAreGeneratedDeterministicallyInRoundRobin() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.samples(-5, 0, 3);
		RandomGenerator<Integer> generator = integerArbitrary.generator(1);
		ArbitraryTestHelper.assertGenerated(generator, -5, 0, 3, -5, 0, 3);
	}

	@Group
	class Numbers {

		@Example
		void integerMinsAndMaxes() {
			RandomGenerator<Integer> generator = Arbitraries.integer().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Integer.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Integer.MAX_VALUE);
		}

		@Example
		void integersInt() {
			Arbitrary<Integer> intArbitrary = Arbitraries.integer(-10, 10);
			RandomGenerator<Integer> generator = intArbitrary.generator(10);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < 0 && value > -5);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 0 && value < 5);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -10 && value <= 10);
		}

		@Example
		void longMinsAndMaxes() {
			RandomGenerator<Long> generator = Arbitraries.longInteger().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Long.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Long.MAX_VALUE);
		}

		@Example
		void integersLong() {
			Arbitrary<Long> longArbitrary = Arbitraries.longInteger(-100L, 100L);
			RandomGenerator<Long> generator = longArbitrary.generator(1000);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -50);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 50);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -100L && value <= 100L);
		}

		@Example
		void bigIntegers() {
			Arbitrary<BigInteger> longArbitrary = Arbitraries.bigInteger(-100L, 100L);
			RandomGenerator<BigInteger> generator = longArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.compareTo(BigInteger.valueOf(50L)) < 0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.compareTo(BigInteger.valueOf(50L)) > 0);
			ArbitraryTestHelper.assertAllGenerated(generator, //
					value -> value.compareTo(BigInteger.valueOf(-100L)) >= 0 //
							&& value.compareTo(BigInteger.valueOf(100L)) <= 0);
		}

		@Example
		void doubleMinsAndMaxes() {
			RandomGenerator<Double> generator = Arbitraries.doubles().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Double.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Double.MAX_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == -Double.MAX_VALUE);
		}

		@Example
		void doubles() {
			Arbitrary<Double> doubleArbitrary = Arbitraries.doubles(-10.0, 10.0, 2);
			RandomGenerator<Double> generator = doubleArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == 0.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -1.0 && value > -9.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 1.0 && value < 9.0);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> {
				double rounded = Math.round(value * 100) / 100.0;
				return value >= -10.0 && value <= 10.0 && value == rounded;
			});
		}

		@Example
		void doublesWithMaximumRange() {
			Arbitrary<Double> doubleArbitrary = Arbitraries.doubles(-Double.MAX_VALUE, Double.MAX_VALUE, 2);
			RandomGenerator<Double> generator = doubleArbitrary.generator(10000);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == 0.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -1000.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 1000.0);
		}

		@Example
		void floatMinsAndMaxes() {
			RandomGenerator<Float> generator = Arbitraries.floats().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Float.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Float.MAX_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == -Float.MAX_VALUE);
		}

		@Example
		void floats() {
			Arbitrary<Float> doubleArbitrary = Arbitraries.floats(-10.0f, 10.0f, 2);
			RandomGenerator<Float> generator = doubleArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == 0.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -1.0 && value > -9.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 1.0 && value < 9.0);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> {
				float rounded = (float) (Math.round(value * 100) / 100.0);
				return value >= -10.0 && value <= 10.0 && value == rounded;
			});
		}

		@Example
		void bigDecimals() {
			Arbitrary<BigDecimal> doubleArbitrary = Arbitraries.bigDecimal(-10.0, 10.0, 2);
			RandomGenerator<BigDecimal> generator = doubleArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.doubleValue() == 0.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.doubleValue() < -1.0 && value.doubleValue() > -9.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.doubleValue() > 1.0 && value.doubleValue() < 9.0);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value.scale() <= 2);
		}

	}

	@Group
	class GenericTypes {

		@Example
		void list() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = Arbitraries.listOf(stringArbitrary, 2, 5);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator, 2, 5);
		}

		@Example
		void set() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<Set<Integer>> listArbitrary = Arbitraries.setOf(integerArbitrary, 2, 7);

			RandomGenerator<Set<Integer>> generator = listArbitrary.generator(1);

			assertGeneratedSet(generator, 2, 7);
		}

		@Example
		void stream() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<Stream<Integer>> streamArbitrary = Arbitraries.streamOf(integerArbitrary, 0, 5);

			RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1);

			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
		}

		@Example
		void optional() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("one", "two");
			Arbitrary<Optional<String>> optionalArbitrary = Arbitraries.optionalOf(stringArbitrary);

			RandomGenerator<Optional<String>> generator = optionalArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("one"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("two"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> !optional.isPresent());
		}

		@Example
		void array() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<Integer[]> arrayArbitrary = Arbitraries.arrayOf(Integer[].class, integerArbitrary, 2,5);

			RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1);

			ArbitraryTestHelper.assertAllGenerated(generator, array -> {
				assertThat(array.length).isBetween(2, 5);
				assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
			});
		}

		@Example
		void arrayOfPrimitiveType() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<int[]> arrayArbitrary = Arbitraries.arrayOf(int[].class, integerArbitrary, 0, 5);

			RandomGenerator<int[]> generator = arrayArbitrary.generator(1);

			Shrinkable<int[]> array = generator.next(random);
			assertThat(array.value().length).isBetween(0, 5);
			List<Integer> actual = IntStream.of(array.value()).boxed().collect(Collectors.toList());
			assertThat(actual).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

	}

	private void assertGeneratedStream(Shrinkable<Stream<Integer>> stream) {
		Set<Integer> set = stream.value().collect(Collectors.toSet());
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private void assertGeneratedSet(RandomGenerator<Set<Integer>> generator, int minSize, int maxSize) {
		ArbitraryTestHelper.assertAllGenerated(generator, set -> {
			assertThat(set.size()).isBetween(minSize, maxSize);
			assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

	private void assertGeneratedLists(RandomGenerator<List<String>> generator, int minSize, int maxSize) {
		ArbitraryTestHelper.assertAllGenerated(generator, list -> {
			assertThat(list.size()).isBetween(minSize, maxSize);
			assertThat(list).isSubsetOf("1", "hallo", "test");
		});
	}

}
