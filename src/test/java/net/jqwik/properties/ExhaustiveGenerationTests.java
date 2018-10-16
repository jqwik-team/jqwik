package net.jqwik.properties;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
@Label("Exhaustive Generation")
class ExhaustiveGenerationTests {

	enum MyEnum {
		Yes,
		No,
		Maybe
	}

	@Example
	@Label("Arbitrary.map()")
	void mapping() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.integers().between(-5, 5).map(i -> Integer.toString(i)).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11);
		assertThat(generator).containsExactly("-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5");
	}

	@Example
	@Label("Arbitrary.filter()")
	void filtering() {
		Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-5, 5)
																			  .filter(i -> i % 2 == 0)
																			  .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(-4, -2, 0, 2, 4);
	}

	@Example
	@Label("Arbitrary.unique(): filter out duplicates")
	void unique() {
		Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.of(1, 2, 1, 3, 1, 2).unique().exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(6); // Cannot know the number of unique elements in advance
		assertThat(generator).containsExactly(1, 2, 3);

		//TODO: Add test for unique used as element arbitrary of list()
	}

	@Example
	@Label("Arbitrary.injectNull(): null is prepended")
	void withNull() {
		double doesNotMatter = 0.5;
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("abc", "def").injectNull(doesNotMatter).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(null, "abc", "def");
	}

	@Example
	@Label("Arbitrary.withSamples(): samples are prepended")
	void withSamples() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator =
			Arbitraries.of("abc", "def")
					   .withSamples("s1", "s2").exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly("s1", "s2", "abc", "def");
	}

	@Example
	@Label("Arbitrary.fixGenSize() has no influence on exhaustive generation")
	void fixGenSize() {
		int doesNotMatter = 42;
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("abc", "def").fixGenSize(doesNotMatter).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly("abc", "def");
	}

	@Group
	class OfValues {

		@Example
		void booleans() {
			Optional<ExhaustiveGenerator<Boolean>> optionalGenerator = Arbitraries.of(true, false).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Boolean> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(2);
			assertThat(generator).containsExactly(true, false);
		}

		@Example
		void samples() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("a", "b", "c", "d").exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly("a", "b", "c", "d");
		}

		@Example
		void enums() {
			Optional<ExhaustiveGenerator<MyEnum>> optionalGenerator = Arbitraries.of(MyEnum.class).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<MyEnum> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(MyEnum.Yes, MyEnum.No, MyEnum.Maybe);
		}

	}

	@Group
	class Integers {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-10, 10).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(21);
			assertThat(generator).containsExactly(-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

		@Example
		void rangeTooBig() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-1, Integer.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class Longs {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<Long>> optionalGenerator = Arbitraries.longs().between(-10, 10).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Long> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(21);
			assertThat(generator).containsExactly(-10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		}

		@Example
		void rangeTooBig() {
			Optional<ExhaustiveGenerator<Long>> optionalGenerator = Arbitraries.longs().between(-1, Long.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class BigIntegers {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<BigInteger>> optionalGenerator = Arbitraries.bigIntegers().between(BigInteger.valueOf(-2), BigInteger.valueOf(2)).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<BigInteger> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5);
			assertThat(generator).containsExactly(BigInteger.valueOf(-2), BigInteger.valueOf(-1), BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(2));
		}

		@Example
		void rangeTooBig() {
			Optional<ExhaustiveGenerator<BigInteger>> optionalGenerator = Arbitraries.bigIntegers().between(BigInteger.valueOf(Long.MIN_VALUE), BigInteger.ZERO).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class Shorts {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<Short>> optionalGenerator = Arbitraries.shorts().between((short) -5, (short) 5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Short> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(11);
			assertThat(generator).containsExactly((short) -5, (short) -4, (short) -3, (short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Short>> optionalGenerator = Arbitraries.shorts().between(Short.MIN_VALUE, Short.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Short> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(65536);
		}
	}

	@Group
	class Bytes {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<Byte>> optionalGenerator = Arbitraries.bytes().between((byte) -5, (byte) 5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Byte> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(11);
			assertThat(generator).containsExactly((byte) -5, (byte) -4, (byte) -3, (byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5);
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Byte>> optionalGenerator = Arbitraries.bytes().between(Byte.MIN_VALUE, Byte.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Byte> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(256);
		}
	}

	@Group
	class Chars {
		@Example
		void fromMinToMax() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.chars().between('a', 'f').exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6);
			assertThat(generator).containsExactly('a', 'b', 'c', 'd', 'e', 'f');
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.chars().between(Character.MIN_VALUE, Character.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(65536);
		}

		@Example
		@Label("Arbitraries.of(char[])")
		void arbitrariesOf() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.of(new char[] {'a', 'c', 'e', 'X'}).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly('a', 'c', 'e', 'X');
		}
	}

	@Group
	class Lists {
		@Example
		void listsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).list().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			assertThat(generator).containsExactly(
				Arrays.asList(),
				Arrays.asList(1),
				Arrays.asList(2),
				Arrays.asList(3),
				Arrays.asList(1, 1),
				Arrays.asList(1, 2),
				Arrays.asList(1, 3),
				Arrays.asList(2, 1),
				Arrays.asList(2, 2),
				Arrays.asList(2, 3),
				Arrays.asList(3, 1),
				Arrays.asList(3, 2),
				Arrays.asList(3, 3)
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<List<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).list().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 10).list().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}
}
