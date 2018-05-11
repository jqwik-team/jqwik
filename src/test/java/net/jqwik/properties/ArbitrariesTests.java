package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.assertj.core.api.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

@Label("Arbitraries")
class ArbitrariesTests {

	enum MyEnum {
		Yes,
		No,
		Maybe
	}

	private Random random = SourceOfRandomness.current();

	@Example
	void randomValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.randomValue(random -> Integer.toString(random.nextInt(10)));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Integer.parseInt(value) < 10);
		ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, "1", "2", "3", "4", "5", "6", "7", "8", "9");
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
		ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, "1", "hallo", "test");
	}

	@Example
	void ofValueList() {
		List<String> valueList = Arrays.asList("1", "hallo", "test");
		Arbitrary<String> stringArbitrary = Arbitraries.of(valueList);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, (String value) -> Arrays.asList("1", "hallo", "test").contains(value));
		ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, "1", "hallo", "test");
	}

	@Example
	void ofEnum() {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);
		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, (MyEnum value) -> Arrays.asList(MyEnum.class.getEnumConstants()).contains(value));
		ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, MyEnum.values());
	}

	@Example
	void samplesAreGeneratedDeterministicallyInRoundRobin() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.samples(-5, 0, 3);
		RandomGenerator<Integer> generator = integerArbitrary.generator(1);
		ArbitraryTestHelper.assertGenerated(generator, -5, 0, 3, -5, 0, 3);
	}

	@Example
	void randoms() {
		Arbitrary<Random> randomArbitrary = Arbitraries.randoms();
		RandomGenerator<Random> generator = randomArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, (Random value) -> value.nextInt(100) < 100);
	}

	@Example
	void constant() {
		Arbitrary<String> constant = Arbitraries.constant("hello");
		ArbitraryTestHelper.assertAllGenerated(constant.generator(1000), value -> {
			Assertions.assertThat(value).isEqualTo("hello");
		});
	}

	@Example
	void oneOf() {
		Arbitrary<Integer> one = Arbitraries.of(1);
		Arbitrary<Integer> two = Arbitraries.of(2);
		Arbitrary<Integer> threeToFive = Arbitraries.of(3, 4, 5);

		Arbitrary<Integer> oneOfArbitrary = Arbitraries.oneOf(one, two, threeToFive);
		ArbitraryTestHelper.assertAllGenerated(oneOfArbitrary.generator(1000), value -> {
			Assertions.assertThat(value).isIn(1, 2, 3, 4, 5);
		});

		RandomGenerator<Integer> generator = oneOfArbitrary.generator(1000);
		ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, 1, 2, 3, 4, 5);
	}

	@Example
	void lazy() {
		Arbitrary<Integer> samples = Arbitraries.lazy(() -> Arbitraries.samples(1, 2, 3));

		ArbitraryTestHelper.assertGenerated(samples.generator(1000), 1, 2, 3, 1);
		ArbitraryTestHelper.assertGenerated(samples.generator(1000), 1, 2, 3, 1);
	}

	@Group
	@Label("frequency")
	class Frequency {

		@Example
		void onePair() {
			Arbitrary<String> one = Arbitraries.frequency(Tuples.tuple(1, "a"));
			ArbitraryTestHelper.assertAllGenerated(one.generator(1000), value -> {return value.equals("a");});
		}

		@Property(tries = 10)
		void twoEqualPairs() {
			Arbitrary<String> one = Arbitraries.frequency(Tuples.tuple(1, "a"), Tuples.tuple(1, "b"));
			Map<String, Integer> counts = ArbitraryTestHelper.count(one.generator(1000), 1000);
			Assertions.assertThat(counts.get("a") > 200).isTrue();
			Assertions.assertThat(counts.get("b") > 200).isTrue();
		}

		@Property(tries = 10)
		void twoUnequalPairs() {
			Arbitrary<String> one = Arbitraries.frequency(Tuples.tuple(1, "a"), Tuples.tuple(10, "b"));
			Map<String, Integer> counts = ArbitraryTestHelper.count(one.generator(1000), 1000);
			Assertions.assertThat(counts.get("a")).isLessThan(counts.get("b"));
		}

		@Property(tries = 10)
		void fourUnequalPairs() {
			Arbitrary<String> one = Arbitraries.frequency(
				Tuples.tuple(1, "a"),
				Tuples.tuple(5, "b"),
				Tuples.tuple(10, "c"),
				Tuples.tuple(20, "d")
			);
			Map<String, Integer> counts = ArbitraryTestHelper.count(one.generator(1000), 1000);
			Assertions.assertThat(counts.get("a")).isLessThan(counts.get("b"));
			Assertions.assertThat(counts.get("b")).isLessThan(counts.get("c"));
			Assertions.assertThat(counts.get("c")).isLessThan(counts.get("d"));
		}

		@Example
		void noPositiveFrequencies() {
			assertThatThrownBy(() -> Arbitraries.frequency(Tuples.tuple(0, "a"))).isInstanceOf(JqwikException.class);
		}

	}

	@Group
	@Label("defaultFor")
	class DefaultFor {
		@Example
		void simpleType() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.defaultFor(Integer.class);
			ArbitraryTestHelper.assertAllGenerated(integerArbitrary.generator(1000), Objects::nonNull);
		}

		@Example
		void parameterizedType() {
			Arbitrary<List> list = Arbitraries.defaultFor(List.class, String.class);
			ArbitraryTestHelper.assertAllGenerated(list.generator(1000), List.class::isInstance);
		}

		@Property
		boolean defaultForParameterizedType(@ForAll("stringLists") @Size(max = 50) List<?> stringList) {
			return stringList.isEmpty() || stringList.get(0) instanceof String;
		}

		@Provide
		Arbitrary<List> stringLists() {
			return Arbitraries.defaultFor(List.class, String.class);
		}
	}

	@Group
	@Label("chars")
	class Chars {
		@Example
		void charsDefault() {
			Arbitrary<Character> arbitrary = Arbitraries.chars();
			RandomGenerator<Character> generator = arbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, Objects::nonNull);
		}

		@Example
		void chars() {
			Arbitrary<Character> arbitrary = Arbitraries.chars().between('a', 'd');
			RandomGenerator<Character> generator = arbitrary.generator(1);
			List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
			ArbitraryTestHelper.assertAllGenerated(generator, (Character value) -> allowedChars.contains(value));
		}
	}

	@Group
	class Strings {
		@Example
		void string() {
			Arbitrary<String> stringArbitrary = Arbitraries.strings() //
														   .withCharRange('a', 'd') //
														   .ofMinLength(0).ofMaxLength(5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, 0, 5);
		}

		@Property
		void stringWithFixedLength(@ForAll @IntRange(min = 1, max = 10) int size) {
			Arbitrary<String> stringArbitrary = Arbitraries.strings() //
														   .withCharRange('a', 'a') //
														   .ofMinLength(size).ofMaxLength(size);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value.length() == size);
			ArbitraryTestHelper.assertAllGenerated(generator, (String value) -> value.chars().allMatch(i -> i == 'a'));
		}

		@Example
		void stringFromCharset() {
			char[] validChars = new char[] { 'a', 'b', 'c', 'd' };
			Arbitrary<String> stringArbitrary = Arbitraries.strings() //
														   .withChars(validChars) //
														   .ofMinLength(2).ofMaxLength(5);
			RandomGenerator<String> generator = stringArbitrary.generator(1);
			assertGeneratedString(generator, 2, 5);
		}

	}

	@Group
	class Numbers {

		@Example
		void shorts() {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts();
			RandomGenerator<Short> generator = enumArbitrary.generator(100);
			ArbitraryTestHelper.assertAllGenerated(generator, (Short value) -> value >= Short.MIN_VALUE && value <= Short.MAX_VALUE);
		}

		@Example
		void shortsMinsAndMaxes() {
			Arbitrary<Short> enumArbitrary = Arbitraries.shorts().between((short) -10, (short) 10);
			RandomGenerator<Short> generator = enumArbitrary.generator(100);

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
			Arbitrary<Byte> enumArbitrary = Arbitraries.bytes().between((byte) -10, (byte) 10);
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
			Arbitrary<Integer> intArbitrary = Arbitraries.integers().between(-10, 10);
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
			Arbitrary<Long> longArbitrary = Arbitraries.longs().between(-100L, 100L);
			RandomGenerator<Long> generator = longArbitrary.generator(1000);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -50);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 50);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -100L && value <= 100L);
		}

		@Example
		void bigIntegers() {
			Arbitrary<BigInteger> longArbitrary = Arbitraries.bigIntegers() //
															 .between(BigInteger.valueOf(-100L), BigInteger.valueOf(100L));
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
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == -Double.MAX_VALUE);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value == Double.MAX_VALUE);
		}

		@Example
		void doubles() {
			Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().between(-10.0, 10.0).ofScale(2);
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
			double min = -Double.MAX_VALUE;
			Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().between(min, Double.MAX_VALUE).ofScale(2);
			RandomGenerator<Double> generator = doubleArbitrary.generator(10000);

			ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, 0.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -1000.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 1000.0);
		}

		@Example
		void floatMinsAndMaxes() {
			RandomGenerator<Float> generator = Arbitraries.floats().generator(1);
			ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, 0.01f, -0.01f, -Float.MAX_VALUE, Float.MAX_VALUE);
		}

		@Example
		void floats() {
			Arbitrary<Float> floatArbitrary = Arbitraries.floats().between(-10.0f, 10.0f).ofScale(2);
			RandomGenerator<Float> generator = floatArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGeneratedOf(generator, 0.0f);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -1.0 && value > -9.0);
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 1.0 && value < 9.0);
			ArbitraryTestHelper.assertAllGenerated(generator, value -> {
				float rounded = (float) (Math.round(value * 100) / 100.0);
				return value >= -10.0 && value <= 10.0 && value == rounded;
			});
		}

		@Example
		void bigDecimals() {
			Arbitrary<BigDecimal> arbitrary = Arbitraries.bigDecimals() //
														 .between(new BigDecimal(-10.0), new BigDecimal(10.0)) //
														 .ofScale(2);
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
			Arbitrary<List<String>> listArbitrary = stringArbitrary.list().ofMinSize(2).ofMaxSize(5);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator, 2, 5);
		}

		@Example
		void set() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Set<Integer>> listArbitrary = integerArbitrary.set().ofMinSize(2).ofMaxSize(7);

			RandomGenerator<Set<Integer>> generator = listArbitrary.generator(1);

			assertGeneratedSet(generator, 2, 7);
		}

		@Example
		void stream() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Stream<Integer>> streamArbitrary = integerArbitrary.stream().ofMinSize(0).ofMaxSize(5);

			RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1);

			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
		}

		@Example
		void optional() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("one", "two");
			Arbitrary<Optional<String>> optionalArbitrary = stringArbitrary.optional();

			RandomGenerator<Optional<String>> generator = optionalArbitrary.generator(1);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("one"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("two"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, optional -> !optional.isPresent());
		}

		@Example
		void array() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Integer[]> arrayArbitrary = integerArbitrary.array(Integer[].class).ofMinSize(2).ofMaxSize(5);

			RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1);

			ArbitraryTestHelper.assertAllGenerated(generator, array -> {
				assertThat(array.length).isBetween(2, 5);
				assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
			});
		}

		@Example
		void arrayOfPrimitiveType() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<int[]> arrayArbitrary = integerArbitrary.array(int[].class).ofMinSize(0).ofMaxSize(5);

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
				(String value) -> value.chars().allMatch(i -> allowedChars.contains((char) i)));
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
