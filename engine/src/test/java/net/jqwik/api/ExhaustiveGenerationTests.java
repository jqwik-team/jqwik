package net.jqwik.api;

import java.math.*;
import java.util.ArrayList;
import java.util.*;

import static java.math.RoundingMode.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
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
		Optional<ExhaustiveGenerator<String>> optionalGenerator =
			Arbitraries.integers().between(-5, 5).map(i -> Integer.toString(i))
					   .exhaustive();
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
	@Label("Arbitrary.ignoreException()")
	void ignoringException() {
		Arbitrary<Integer> arbitrary =
			Arbitraries.integers().between(-5, 5)
					   .map(anInt -> {
						   if (anInt % 2 != 0) {
							   throw new IllegalArgumentException("No even numbers");
						   }
						   return anInt;
					   });
		Arbitrary<Integer> filtered = arbitrary.ignoreException(IllegalArgumentException.class);

		Optional<ExhaustiveGenerator<Integer>> optionalGenerator = filtered.exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11); // Cannot know the number of thrown exceptions in advance
		assertThat(generator).containsExactly(-4, -2, 0, 2, 4);
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
	@Label("Arbitrary.fixGenSize() has no influence on exhaustive generation")
	void fixGenSize() {
		int doesNotMatter = 42;
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("abc", "def").fixGenSize(doesNotMatter).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly("abc", "def");
	}

	@Example
	@Label("Arbitrary.flatMap()")
	void flatMapping() {
		Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
			Arbitraries.integers().between(1, 3)
					   .flatMap(i -> Arbitraries.integers().between(1, 2)
												.list().ofSize(i)
					   ).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(14);
		assertThat(generator).containsOnly(
			asList(1),
			asList(2),
			asList(1, 1),
			asList(2, 2),
			asList(1, 2),
			asList(2, 1),
			asList(1, 1, 1),
			asList(1, 1, 2),
			asList(1, 2, 1),
			asList(1, 2, 2),
			asList(2, 1, 1),
			asList(2, 1, 2),
			asList(2, 2, 1),
			asList(2, 2, 2)
		);
	}

	@Example
	@Label("Arbitrary.flatMap() will freshly generate base values")
	void flatMapWillFreshlyGenerateBaseValues() {
		Set<Object> dates = new LinkedHashSet<>();

		Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
			Arbitraries.create(Object::new).flatMap(object -> {
				dates.add(object);
				return Arbitraries.of(1, 2, 3);
			}).exhaustive();

		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsOnly(1, 2, 3);

		assertThat(dates.size()).isGreaterThan(1);
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
		void values() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("a", "b", "c", "d").exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly("a", "b", "c", "d");
		}

		@Example
		@Label("Arbitraries.frequency() returns all in row")
		void frequency() {
			Tuple.Tuple2<Integer, String> frequency1 = Tuple.of(1, "a");
			Tuple.Tuple2<Integer, String> frequency2 = Tuple.of(2, "b");
			Tuple.Tuple2<Integer, String> frequency3 = Tuple.of(3, "c");
			Tuple.Tuple2<Integer, String> frequency0 = Tuple.of(0, "d");

			Optional<ExhaustiveGenerator<String>> optionalGenerator =
				Arbitraries.frequency(frequency1, frequency2, frequency3, frequency0)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly("a", "b", "c");
		}

		@Example
		void enums() {
			Optional<ExhaustiveGenerator<MyEnum>> optionalGenerator = Arbitraries.of(MyEnum.class).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<MyEnum> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(MyEnum.Yes, MyEnum.No, MyEnum.Maybe);
		}

		@Example
		void withNulls() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.of("string1", null, "string3").exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly("string1", null, "string3");
		}

	}

	@Group
	class FloatsAndDecimals {

		@Example
		void singleBigDecimal() {
			Optional<ExhaustiveGenerator<BigDecimal>> optionalGenerator =
				Arbitraries.bigDecimals()
						   .between(BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0))
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<BigDecimal> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(BigDecimal.valueOf(100.0));
		}

		@Example
		void bigDecimalRangeDoesNotAllowExhaustiveGeneration() {
			Optional<ExhaustiveGenerator<BigDecimal>> optionalGenerator =
				Arbitraries.bigDecimals()
						   .between(BigDecimal.ONE, BigDecimal.valueOf(100.0))
						   .exhaustive();
			assertThat(optionalGenerator).isEmpty();
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
			assertThat(generator)
				.containsExactly(-10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
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
			Optional<ExhaustiveGenerator<BigInteger>> optionalGenerator =
				Arbitraries.bigIntegers()
						   .between(BigInteger.valueOf(-2), BigInteger.valueOf(2))
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<BigInteger> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5);
			assertThat(generator)
				.containsExactly(BigInteger.valueOf(-2), BigInteger.valueOf(-1), BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger
																																   .valueOf(2));
		}

		@Example
		void rangeTooBig() {
			Optional<ExhaustiveGenerator<BigInteger>> optionalGenerator = Arbitraries.bigIntegers().between(BigInteger
																												.valueOf(Long.MIN_VALUE), BigInteger.ZERO)
																					 .exhaustive();
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
			assertThat(generator)
				.containsExactly((short) -5, (short) -4, (short) -3, (short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Short>> optionalGenerator = Arbitraries.shorts().between(Short.MIN_VALUE, Short.MAX_VALUE)
																				.exhaustive();
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
			assertThat(generator)
				.containsExactly((byte) -5, (byte) -4, (byte) -3, (byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5);
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Byte>> optionalGenerator = Arbitraries.bytes().between(Byte.MIN_VALUE, Byte.MAX_VALUE)
																			   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Byte> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(256);
		}
	}

	@Group
	class Chars {
		@Example
		void range() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.chars().range('a', 'f').exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6);
			assertThat(generator).containsExactly('a', 'b', 'c', 'd', 'e', 'f');
		}

		@Example
		void with() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.chars().with('a', 'c', 'e').exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly('a', 'c', 'e');
		}

		@Example
		void withAndRange() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator =
				Arbitraries.chars()
						   .with('a', 'c', 'e')
						   .range('1', '4')
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly('a', 'c', 'e', '1', '2', '3', '4');
		}

		@Example
		void rangeCannotBeTooBig() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.chars().range(Character.MIN_VALUE, Character.MAX_VALUE)
																					.exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(65536);
		}

		@Example
		@Label("Arbitraries.of(char[])")
		void arbitrariesOf() {
			Optional<ExhaustiveGenerator<Character>> optionalGenerator = Arbitraries.of(new char[]{'a', 'c', 'e', 'X'}).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Character> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly('a', 'c', 'e', 'X');
		}
	}

	@Group
	@Label("Optional")
	class OptionalTests {
		@Example
		void prependsOptionalEmpty() {
			Optional<ExhaustiveGenerator<java.util.Optional<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 5).optional().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Optional<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6);
			assertThat(generator).containsExactly(
				Optional.empty(),
				Optional.of(1),
				Optional.of(2),
				Optional.of(3),
				Optional.of(4),
				Optional.of(5)
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Optional<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).optional().exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Example
	@Label("Arbitraries.defaultFor() returns the edge cases of the resolve arbitrary")
	void defaultFor() {
		Optional<ExhaustiveGenerator<RoundingMode>> optionalGenerator = Arbitraries.defaultFor(RoundingMode.class).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<RoundingMode> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(8);
		assertThat(generator).containsExactlyInAnyOrder(
				UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN, UNNECESSARY
		);
	}

	@Example
	@Label("Arbitraries.lazy() returns the edge cases of the resolve arbitrary")
	void lazy() {
		Optional<ExhaustiveGenerator<RoundingMode>> optionalGenerator = Arbitraries.lazy(
				() -> Arbitraries.of(RoundingMode.class)
		).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<RoundingMode> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(8);
		assertThat(generator).containsExactlyInAnyOrder(
				UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN, UNNECESSARY
		);
	}

	@Example
	@Label("Arbitraries.just() returns the constant once")
	void just() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.just("abc").exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(1);
		assertThat(generator).containsExactly("abc");
	}

	@Example
	@Label("Arbitraries.create() returns the created value once")
	void create() {
		Optional<ExhaustiveGenerator<Object>> optionalGenerator = Arbitraries.create(() -> new Object()).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Object> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(1);

		Object first = generator.iterator().next();
		assertThat(first).isInstanceOf(Object.class);
		assertThat(first).isNotSameAs(generator.iterator().next());
	}

	@Example
	@Label("Arbitraries.shuffle() returns all permutations")
	void shuffle() {
		Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator = Arbitraries.shuffle(1, 2, 3).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(6);
		assertThat(generator).containsOnly(
			asList(1, 2, 3),
			asList(1, 3, 2),
			asList(2, 3, 1),
			asList(2, 1, 3),
			asList(3, 1, 2),
			asList(3, 2, 1)
		);
	}

	@Example
	@Label("Arbitraries.oneOf()")
	void oneOf() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.oneOf(
			Arbitraries.of("a", "b"),
			Arbitraries.of("c", "d"),
			Arbitraries.just("e")
		).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5);
		assertThat(generator).containsOnly("a", "b", "c", "d", "e");
	}

	@Example
	@Label("Arbitraries.frequencyOf()")
	void frequencyOf() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.frequencyOf(
			Tuple.of(1, Arbitraries.of("a", "b")),
			Tuple.of(2, Arbitraries.of("c", "d")),
			Tuple.of(3, Arbitraries.just("e"))
		).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5);
		assertThat(generator).containsOnly("a", "b", "c", "d", "e");
	}

	@Group
	@Label("Arbitraries.strings()")
	class Strings {
		@Example
		void generateAllPossibleStrings() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator =
				Arbitraries.strings().withChars('a', 'b').ofMinLength(0).ofMaxLength(2)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsOnly("", "a", "b", "aa", "bb", "ab", "ba");
		}

		@Example
		void allNumberStringsWith5Digits() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator =
				Arbitraries.strings().numeric().ofLength(5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(100000);
			assertThat(generator).contains("00000", "12345", "98765", "99999");
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator =
				Arbitraries.strings().alpha().exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}

	static class AdditionBuilder {

		private final List<Integer> numbers = new ArrayList<>();

		AdditionBuilder addNumber(int number) {
			numbers.add(number);
			return this;
		}

		int sum() {
			return numbers.stream().mapToInt(n -> n).sum();
		}
	}
}
