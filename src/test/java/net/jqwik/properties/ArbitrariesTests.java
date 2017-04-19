package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class ArbitrariesTests {

	enum MyEnum {
		Yes,
		No,
		Maybe
	}

	private Random random = new Random();

	@Example
	void fromGenerator() {
		Arbitrary<String> stringArbitrary = Arbitraries.fromGenerator(random -> Integer.toString(random.nextInt(10)));

		RandomGenerator<String> generator = stringArbitrary.generator(1);

		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
	}

	@Example
	void ofValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");

		RandomGenerator<String> generator = stringArbitrary.generator(1);

		assertThat(generator.next(random)).isIn("1", "hallo", "test");
		assertThat(generator.next(random)).isIn("1", "hallo", "test");
		assertThat(generator.next(random)).isIn("1", "hallo", "test");
		assertThat(generator.next(random)).isIn("1", "hallo", "test");
	}

	@Example
	void ofEnum() {
		Arbitrary<MyEnum> enumArbitrary = Arbitraries.of(MyEnum.class);

		RandomGenerator<MyEnum> generator = enumArbitrary.generator(1);

		assertThat(generator.next(random)).isIn((Object[]) MyEnum.class.getEnumConstants());
		assertThat(generator.next(random)).isIn((Object[]) MyEnum.class.getEnumConstants());
		assertThat(generator.next(random)).isIn((Object[]) MyEnum.class.getEnumConstants());
	}

	@Example
	void string() {
		Arbitrary<String> stringArbitrary = Arbitraries.string('a', 'd', 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);

		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
	}

	@Example
	void stringFromCharset() {
		char[] validChars = new char[] { 'a', 'b', 'c', 'd' };
		Arbitrary<String> stringArbitrary = Arbitraries.string(validChars, 5);
		RandomGenerator<String> generator = stringArbitrary.generator(1);

		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
		assertGeneratedString(generator.next(random));
	}

	@Example
	void integersInt() {
		Arbitrary<Integer> intArbitrary = Arbitraries.integer(-10, 10);
		RandomGenerator<Integer> generator = intArbitrary.generator(1);

		assertAtLeastOneGenerated(generator, value -> ((int) value) < -5);
		assertAtLeastOneGenerated(generator, value -> ((int) value) > 5);
		assertAllGenerated(generator, value -> {
			int intValue = (int) value;
			return intValue >= -10 && intValue <= 10;
		});
	}

	@Example
	void integersLong() {
		Arbitrary<Long> longArbitrary = Arbitraries.integer(-100L, 100L);
		RandomGenerator<Long> generator = longArbitrary.generator(1);

		assertAtLeastOneGenerated(generator, value -> ((long) value) < -50);
		assertAtLeastOneGenerated(generator, value -> ((long) value) > 50);
		assertAllGenerated(generator, value -> {
			long intValue = (long) value;
			return intValue >= -100L && intValue <= 100L;
		});
	}

	@Group
	class GenericTypes {

		@Example
		void list() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = Arbitraries.listOf(stringArbitrary, 5);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);

			assertGeneratedList(generator.next(random));
			assertGeneratedList(generator.next(random));
			assertGeneratedList(generator.next(random));
			assertGeneratedList(generator.next(random));
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

			assertOptionalString(generator.next(random));
			assertOptionalString(generator.next(random));
			assertOptionalString(generator.next(random));
			assertOptionalString(generator.next(random));
		}

		@Example
		void optionalAlsoGeneratesNulls() {
			Arbitrary<Optional<String>> optionalArbitrary = Arbitraries.optionalOf(Arbitraries.of("one"));
			RandomGenerator<Optional<String>> generator = optionalArbitrary.generator(1);

			for (int i = 0; i < 100; i++) {
				if (!generator.next(random).isPresent())
					return;
			}
			Assertions.fail("Optional with null should have been created");
		}

	}

	private void assertAtLeastOneGenerated(RandomGenerator generator, Function<Object, Boolean> checker) {
		for (int i = 0; i < 100; i++) {
			Object value = generator.next(random);
			if (checker.apply(value))
				return;
		}
		fail("Failed to generate at least one");
	}

	private void assertAllGenerated(RandomGenerator generator, Function<Object, Boolean> checker) {
		for (int i = 0; i < 100; i++) {
			Object value = generator.next(random);
			if (!checker.apply(value))
				fail(String.format("Value [%s] failed to fullfil condition.", value.toString()));
		}
	}


	private void assertGeneratedStream(Stream<Integer> stream) {
		Set<Integer> set = stream.collect(Collectors.toSet());
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private void assertGeneratedSet(Set<Integer> set) {
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private void assertOptionalString(Optional<String> optional) {
		assertThat(optional).isInstanceOf(Optional.class);
		if (optional.isPresent())
			assertThat(optional.get()).isIn("one", "two");
	}

	private void assertGeneratedString(String value) {
		assertThat(value.length()).isBetween(0, 5);
		Set<Character> characterSet = value.chars().mapToObj(e -> (char) e).collect(Collectors.toSet());
		assertThat(characterSet).isSubsetOf('a', 'b', 'c', 'd');
	}

	private void assertGeneratedList(List<String> list) {
		assertThat(list.size()).isBetween(0, 5);
		assertThat(list).isSubsetOf("1", "hallo", "test");
	}

	private void assertIntStringLessThan10(String next) {
		assertThat(next).isInstanceOf(String.class);
		assertThat(Integer.parseInt(next)).isLessThan(10);
	}
}
