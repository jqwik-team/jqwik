package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class ArbitrariesTests {

	enum MyEnum {
		Yes, No, Maybe
	}

	private Random random = new Random();

	@Example
	void fromGenerator() {
		Arbitrary<String> stringArbitrary = Arbitraries.fromGenerator(random -> Shrinkable.unshrinkable(Integer.toString(random.nextInt(10))));
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Integer.parseInt(value) < 10);
	}

	@Example
	void ofValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Arrays.asList("1", "hallo", "test").contains(value));
	}

	@Example
	void ofEnum() {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);
		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> Arrays.asList(MyEnum.class.getEnumConstants()).contains(value));
	}

	@Example
	void integersInt() {
		Arbitrary<Integer> intArbitrary = Arbitraries.integer(-10, 10);
		RandomGenerator<Integer> generator = intArbitrary.generator(1);

		ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -5);
		ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 5);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -10 && value <= 10);
	}

	@Example
	void integersLong() {
		Arbitrary<Long> longArbitrary = Arbitraries.longInteger(-100L, 100L);
		RandomGenerator<Long> generator = longArbitrary.generator(1);

		ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value < -50);
		ArbitraryTestHelper.assertAtLeastOneGenerated(generator, value -> value > 50);
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value >= -100L && value <= 100L);
	}

	@Example
	void string() {
		Arbitrary<String> stringArbitrary = Arbitraries.string('a', 'd', 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator);
	}

	@Example
	void stringFromCharset() {
		char[] validChars = new char[]{'a', 'b', 'c', 'd'};
		Arbitrary<String> stringArbitrary = Arbitraries.string(validChars, 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator);
	}

	private void assertGeneratedString(RandomGenerator<String> generator) {
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value.length() >= 0 && value.length() <= 5);
		List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
		ArbitraryTestHelper.assertAllGenerated(generator, value -> value.chars().allMatch(i -> allowedChars.contains(Character.valueOf((char) i))));
	}

	@Example
	void samplesAreGeneratedDeterministicallyInRoundRobin() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.samples(-5, 0, 3);
		RandomGenerator<Integer> generator = integerArbitrary.generator(1);
		ArbitraryTestHelper.assertGenerated(generator, -5, 0, 3, -5, 0, 3);
	}

	@Group
	class GenericTypes {

		@Example
		void list() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = Arbitraries.listOf(stringArbitrary, 5);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator);
		}

		@Example
		void set() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<Set<Integer>> listArbitrary = Arbitraries.setOf(integerArbitrary, 5);

			RandomGenerator<Set<Integer>> generator = listArbitrary.generator(1);

			assertGeneratedSet(generator.next(random));
		}

		 @Example
		 void stream() {
		 Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
		 Arbitrary<Stream<Integer>> streamArbitrary = Arbitraries.streamOf(integerArbitrary, 5);

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
			Arbitrary<Integer[]> arrayArbitrary = Arbitraries.arrayOf(Integer[].class, integerArbitrary, 5);

			RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1);

			Shrinkable<Integer[]> array = generator.next(random);
			assertThat(array.value().length).isBetween(0, 5);
			assertThat(array.value()).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

		@Example
		void arrayOfPrimitiveType() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integer(1, 10);
			Arbitrary<int[]> arrayArbitrary = Arbitraries.arrayOf(int[].class, integerArbitrary, 5);

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

	private void assertGeneratedSet(Shrinkable<Set<Integer>> set) {
		assertThat(set.value().size()).isBetween(0, 5);
		assertThat(set.value()).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private void assertGeneratedLists(RandomGenerator<List<String>> generator) {
		ArbitraryTestHelper.assertAllGenerated(generator, aString -> aString.size() >= 0 && aString.size() <= 5);
		List<String> allowedStrings = Arrays.asList("1", "hallo", "test");
		ArbitraryTestHelper.assertAllGenerated(generator, aString -> aString.stream().allMatch(allowedStrings::contains));
	}

}
