package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

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
	void samplesAreGeneratedDeterministicallyInRoundRobin() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.samples(-5, 0, 3);
		RandomGenerator<Integer> generator = integerArbitrary.generator(1);
		ArbitraryTestHelper.assertGenerated(generator, -5, 0, 3, -5, 0, 3);
	}

	@Group
	class Chars {
		@Example
		void charsDefault() {
			Arbitrary<Character> arbitrary = Arbitraries.chars();
			RandomGenerator<Character> generator = arbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, Objects::nonNull);
		}

		@Example
		void chars() {
			Arbitrary<Character> arbitrary = Arbitraries.chars('a', 'd');
			RandomGenerator<Character> generator = arbitrary.generator(1);
			List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
			ArbitraryTestHelper.assertAllGenerated(generator, (Character value) -> allowedChars.contains(value));
		}

		@Example
		void charsFromCharset() {
			char[] validChars = new char[] { 'a', 'b', 'c', 'd' };
			Arbitrary<Character> stringArbitrary = Arbitraries.chars(validChars);
			RandomGenerator<Character> generator = stringArbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator,
					(Character value) -> String.valueOf(validChars).contains(String.valueOf(value)));
		}
	}

	@Group
	class Strings {
		@Example
		void string() {
			Arbitrary<String> stringArbitrary = Arbitraries.strings('a', 'd', 0, 5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, 0, 5);
		}

		@Property
		void stringWithFixedLength(@ForAll @IntRange(min = 1, max = 10) int size) {
			Arbitrary<String> stringArbitrary = Arbitraries.strings('a', 'a', size, size);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value.length() == size);
			ArbitraryTestHelper.assertAllGenerated(generator, (String value) -> value.chars().allMatch(i -> i == 'a'));
		}

		@Example
		void stringFromCharset() {
			char[] validChars = new char[] { 'a', 'b', 'c', 'd' };
			Arbitrary<String> stringArbitrary = Arbitraries.strings(validChars, 2, 5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, 2, 5);
		}

	}

	@Group
	class Numbers {

		@Example
		void shorts() {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts();
			RandomGenerator<Short> generator = enumArbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, (Short value) -> value >= Short.MIN_VALUE && value <= Short.MAX_VALUE);
		}

		@Example
		void shortsMinsAndMaxes() {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts((short) -10, (short) 10);
			RandomGenerator<Short> generator = enumArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < 0 && value > -5);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 0 && value < 5);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -10 && value <= 10);
		}

		@Example
		void bytes() {
			Arbitrary<Byte> enumArbitrary = Arbitraries.bytes();
			RandomGenerator<Byte> generator = enumArbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, (Byte value) -> value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE);
		}

		@Example
		void bytesMinsAndMaxes() {
			Arbitrary<Byte> enumArbitrary = Arbitraries.bytes((byte) -10, (byte) 10);
			RandomGenerator<Byte> generator = enumArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < 0 && value > -5);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 0 && value < 5);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -10 && value <= 10);
		}

		@Example
		void integerMinsAndMaxes() {
			RandomGenerator<Integer> generator = Arbitraries.integers().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Integer.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Integer.MAX_VALUE);
		}

		@Example
		void integersInt() {
			Arbitrary<Integer> intArbitrary = Arbitraries.integers(-10, 10);
			RandomGenerator<Integer> generator = intArbitrary.generator(10);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < 0 && value > -5);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 0 && value < 5);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -10 && value <= 10);
		}

		@Example
		void longMinsAndMaxes() {
			RandomGenerator<Long> generator = Arbitraries.longs().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Long.MIN_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Long.MAX_VALUE);
		}

		@Example
		void integersLong() {
			Arbitrary<Long> longArbitrary = Arbitraries.longs(-100L, 100L);
			RandomGenerator<Long> generator = longArbitrary.generator(1000);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -50);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 50);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -100L && value <= 100L);
		}

		@Example
		void bigIntegers() {
			Arbitrary<BigInteger> longArbitrary = Arbitraries.bigIntegers(-100L, 100L);
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
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == 0.01);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == -0.01);
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
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == 0.01f);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == -0.01f);
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
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
			RandomGenerator<BigDecimal> generator = arbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.compareTo(BigDecimal.ZERO) == 0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.compareTo(BigDecimal.ONE) == 0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value.compareTo(BigDecimal.ONE.negate()) == 0);
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
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers(1, 10);
			Arbitrary<Set<Integer>> listArbitrary = Arbitraries.setOf(integerArbitrary, 2, 7);

			RandomGenerator<Set<Integer>> generator = listArbitrary.generator(1);

			assertGeneratedSet(generator, 2, 7);
		}

		@Example
		void stream() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers(1, 10);
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
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers(1, 10);
			Arbitrary<Integer[]> arrayArbitrary = Arbitraries.arrayOf(Integer[].class, integerArbitrary, 2, 5);

			RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1);

			ArbitraryTestHelper.assertAllGenerated(generator, array -> {
				assertThat(array.length).isBetween(2, 5);
				assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
			});
		}

		@Example
		void arrayOfPrimitiveType() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers(1, 10);
			Arbitrary<int[]> arrayArbitrary = Arbitraries.arrayOf(int[].class, integerArbitrary, 0, 5);

			RandomGenerator<int[]> generator = arrayArbitrary.generator(1);

			Shrinkable<int[]> array = generator.next(random);
			assertThat(array.value().length).isBetween(0, 5);
			List<Integer> actual = IntStream.of(array.value()).boxed().collect(Collectors.toList());
			assertThat(actual).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

	}

	private void assertGeneratedString(RandomGenerator<String> generator, int minLength, int maxLength) {
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value.length() >= minLength && value.length() <= maxLength);
		List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
		ArbitraryTestHelper.assertAllGenerated(generator,
				(String value) -> value.chars().allMatch(i -> allowedChars.contains(Character.valueOf((char) i))));
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
