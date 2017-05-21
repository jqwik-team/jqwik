package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;

import static net.jqwik.newArbitraries.NArbitraryTestHelper.*;

public class NArbitrariesTests {

	enum MyEnum {
		Yes, No, Maybe
	}

	private Random random = new Random();

	@Example
	void fromGenerator() {
		NArbitrary<String> stringArbitrary = NArbitraries.fromGenerator(random -> NShrinkableValue.unshrinkable(Integer.toString(random.nextInt(10))));
		NShrinkableGenerator<String> generator = stringArbitrary.generator(1);
		assertAllGenerated(generator, value -> Integer.parseInt(value) < 10);
	}

	@Example
	void ofValues() {
		NArbitrary<String> stringArbitrary = NArbitraries.of("1", "hallo", "test");
		NShrinkableGenerator<String> generator = stringArbitrary.generator(1);
		assertAllGenerated(generator, value -> Arrays.asList("1", "hallo", "test").contains(value));
	}

	@Example
	void ofEnum() {
		NArbitrary<MyEnum> enumArbitrary = NArbitraries.of(MyEnum.class);
		NShrinkableGenerator<MyEnum> generator = enumArbitrary.generator(1);
		assertAllGenerated(generator, value -> Arrays.asList(MyEnum.class.getEnumConstants()).contains(value));
	}

	@Example
	void integersInt() {
		NArbitrary<Integer> intArbitrary = NArbitraries.integer(-10, 10);
		NShrinkableGenerator<Integer> generator = intArbitrary.generator(1);

		assertAtLeastOneGenerated(generator, value -> value < -5);
		assertAtLeastOneGenerated(generator, value -> value > 5);
		assertAllGenerated(generator, value -> value >= -10 && value <= 10);
	}

	@Example
	void integersLong() {
		NArbitrary<Long> longArbitrary = NArbitraries.longInteger(-100L, 100L);
		NShrinkableGenerator<Long> generator = longArbitrary.generator(1);

		assertAtLeastOneGenerated(generator, value -> value < -50);
		assertAtLeastOneGenerated(generator, value -> value > 50);
		assertAllGenerated(generator, value -> value >= -100L && value <= 100L);
	}

	@Example
	void string() {
		NArbitrary<String> stringArbitrary = NArbitraries.string('a', 'd', 5);
		NShrinkableGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator);
	}

	@Example
	void stringFromCharset() {
		char[] validChars = new char[]{'a', 'b', 'c', 'd'};
		NArbitrary<String> stringArbitrary = NArbitraries.string(validChars, 5);
		NShrinkableGenerator<String> generator = stringArbitrary.generator(1);
		assertGeneratedString(generator);
	}

	private void assertGeneratedString(NShrinkableGenerator<String> generator) {
		assertAllGenerated(generator, value -> value.length() >= 0 && value.length() <= 5);
		List<Character> allowedChars = Arrays.asList('a', 'b', 'c', 'd');
		assertAllGenerated(generator, value -> value.chars().allMatch(i -> allowedChars.contains(Character.valueOf((char) i))));
	}

	@Example
	void samplesAreGeneratedDeterministicallyInRoundRobin() {
		NArbitrary<Integer> integerArbitrary = NArbitraries.samples(-5, 0, 3);
		NShrinkableGenerator<Integer> generator = integerArbitrary.generator(1);
		assertGenerated(generator, -5, 0, 3, -5, 0, 3);
	}

	@Group
	class GenericTypes {

		@Example
		void list() {
			NArbitrary<String> stringArbitrary = NArbitraries.of("1", "hallo", "test");
			NArbitrary<List<String>> listArbitrary = NArbitraries.listOf(stringArbitrary, 5);

			NShrinkableGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator);
		}

		// @Example
		// void set() {
		// NArbitrary<Integer> integerArbitrary = NArbitraries.integer(1, 10);
		// NArbitrary<Set<Integer>> listArbitrary = NArbitraries.setOf(integerArbitrary, 5);
		//
		// NShrinkableGenerator<Set<Integer>> generator = listArbitrary.generator(1);
		//
		// assertGeneratedSet(generator.next(random));
		// }
		//
		// @Example
		// void stream() {
		// NArbitrary<Integer> integerArbitrary = NArbitraries.integer(1, 10);
		// NArbitrary<Stream<Integer>> streamArbitrary = NArbitraries.streamOf(integerArbitrary, 5);
		//
		// NShrinkableGenerator<Stream<Integer>> generator = streamArbitrary.generator(1);
		//
		// assertGeneratedStream(generator.next(random));
		// assertGeneratedStream(generator.next(random));
		// assertGeneratedStream(generator.next(random));
		// assertGeneratedStream(generator.next(random));
		// }
		//
		// @Example
		// void optional() {
		// NArbitrary<String> stringArbitrary = NArbitraries.of("one", "two");
		// NArbitrary<Optional<String>> optionalArbitrary = NArbitraries.optionalOf(stringArbitrary);
		//
		// NShrinkableGenerator<Optional<String>> generator = optionalArbitrary.generator(1);
		//
		// assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("one"));
		// assertAtLeastOneGenerated(generator, optional -> optional.orElse("").equals("two"));
		// assertAtLeastOneGenerated(generator, optional -> !optional.isPresent());
		// }
		//
		// @Example
		// void array() {
		// NArbitrary<Integer> integerArbitrary = NArbitraries.integer(1, 10);
		// NArbitrary<Integer[]> arrayArbitrary = NArbitraries.arrayOf(Integer[].class, integerArbitrary, 5);
		//
		// NShrinkableGenerator<Integer[]> generator = arrayArbitrary.generator(1);
		//
		// Integer[] array = generator.next(random);
		// assertThat(array.length).isBetween(0, 5);
		// assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// }
		//
		// @Example
		// void arrayOfPrimitiveType() {
		// NArbitrary<Integer> integerArbitrary = NArbitraries.integer(1, 10);
		// NArbitrary<int[]> arrayArbitrary = NArbitraries.arrayOf(int[].class, integerArbitrary, 5);
		//
		// NShrinkableGenerator<int[]> generator = arrayArbitrary.generator(1);
		//
		// int[] array = generator.next(random);
		// assertThat(array.length).isBetween(0, 5);
		// List<Integer> actual = IntStream.of(array).mapToObj(Integer::valueOf).collect(Collectors.toList());
		// assertThat(actual).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// }

	}

	// private void assertGeneratedStream(Stream<Integer> stream) {
	// Set<Integer> set = stream.collect(Collectors.toSet());
	// assertThat(set.size()).isBetween(0, 5);
	// assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	// }
	//
	// private void assertGeneratedSet(Set<Integer> set) {
	// assertThat(set.size()).isBetween(0, 5);
	// assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	// }

	private void assertGeneratedLists(NShrinkableGenerator<List<String>> generator) {
		assertAllGenerated(generator, aString -> aString.size() >= 0 && aString.size() <= 5);
		List<String> allowedStrings = Arrays.asList("1", "hallo", "test");
		assertAllGenerated(generator, aString -> aString.stream().allMatch(allowedStrings::contains));
	}

}
