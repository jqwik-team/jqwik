package net.jqwik.engine.properties;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

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
		Set<Object> dates = new HashSet<>();

		Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
			Arbitraries.create(Object::new).flatMap(object -> {
				dates.add(object);
				return Arbitraries.of(1, 2, 3);
			}).exhaustive();

		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsOnly(1, 2, 3);

		assertThat(dates.size()).isGreaterThanOrEqualTo(3); // maxCount() must also call base
	}

	@Group
	@Label("Arbitrary.unique()")
	class Unique {
		@Example
		@Label("filter out duplicates")
		void unique() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.of(1, 2, 1, 3, 1, 2).unique().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6); // Cannot know the number of unique elements in advance
			assertThat(generator).containsExactly(1, 2, 3);
		}

		@Example
		@Label("uniqueness within list")
		void uniqueWithinList() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator = Arbitraries.of(1, 2, 3).unique().list().ofSize(3).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(27); // Cannot know the number of unique elements in advance
			assertThat(generator).containsOnly(
				asList(1, 2, 3),
				asList(2, 3, 1),
				asList(3, 1, 2),
				asList(1, 3, 2),
				asList(2, 1, 3),
				asList(3, 2, 1)
			);
		}

		@Example
		@Label("uniqueness within list can miss too often")
		void uniqueListSearchMissesTooOften() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.of(1, 2, 3, 4, 5, 6, 7).unique().list().ofSize(8).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();

			assertThatThrownBy(() -> {
				for (List<Integer> integers : generator) { }
			}).isInstanceOf(JqwikException.class);

		}

		@Property
		@Label("reset of uniqueness for embedded arbitraries")
		void uniquenessIsResetForEmbeddedArbitraries(@ForAll("listOfUniqueIntegers") List<Integer> aList) {
			assertThat(aList.size()).isEqualTo(new HashSet<>(aList).size());
		}

		@Provide
		Arbitrary<List<Integer>> listOfUniqueIntegers() {
			return Arbitraries.integers().between(1, 10).unique().list().ofSize(3);
		}
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
		@Label("Arbitraries.samples() returns all samples in row")
		void samples() {
			Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.samples("a", "b", "c").exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<String> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly("a", "b", "c");
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
	class Lists {
		@Example
		void listsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).list().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			assertThat(generator).containsExactly(
				asList(),
				asList(1),
				asList(2),
				asList(3),
				asList(1, 1),
				asList(1, 2),
				asList(1, 3),
				asList(2, 1),
				asList(2, 2),
				asList(2, 3),
				asList(3, 1),
				asList(3, 2),
				asList(3, 3)
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

	@Group
	class Streams {
		@Example
		void streamsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Stream<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 2).stream().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Stream<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator.map(s -> s.collect(Collectors.toList()))).containsExactly(
				asList(),
				asList(1),
				asList(2),
				asList(1, 1),
				asList(1, 2),
				asList(2, 1),
				asList(2, 2)
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Stream<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).stream().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Stream<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 10).stream().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class Arrays {
		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Integer[]>> optionalGenerator =
				Arbitraries.integers().between(1, 2).array(Integer[].class)
						   .ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer[]> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly(
				new Integer[]{},
				new Integer[]{1},
				new Integer[]{2},
				new Integer[]{1, 1},
				new Integer[]{1, 2},
				new Integer[]{2, 1},
				new Integer[]{2, 2}
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Double[]>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).array(Double[].class).ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Integer[]>> optionalGenerator =
				Arbitraries.integers().between(1, 10).array(Integer[].class).ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class Sets {
		@Example
		void setsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).set().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Set<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(3),
				asSet(1, 2),
				asSet(1, 3),
				asSet(2, 3)
			);
		}

		@Example
		void lessElementsThanSetSize() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 2).set().ofMaxSize(5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Set<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(1, 2)
			);
		}

		private Set<Integer> asSet(Integer... ints) {
			return new HashSet<>(asList(ints));
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Set<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).set().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 75).set().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
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

	@Group
	class Maps {
		@Example
		void allCombinationsOfKeysAndValues() {

			IntegerArbitrary keys = Arbitraries.integers().between(1, 3);
			IntegerArbitrary values = Arbitraries.integers().between(4, 5);
			Optional<ExhaustiveGenerator<Map<Integer, Integer>>> mapGenerator =
				Arbitraries.maps(keys, values).ofSize(2).exhaustive();
			assertThat(mapGenerator).isPresent();

			ExhaustiveGenerator<Map<Integer, Integer>> generator = mapGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12);

			assertThat(generator).containsExactlyInAnyOrder(
				createMap(Tuple.of(1, 5), Tuple.of(2, 5)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 5), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 5)),
				createMap(Tuple.of(1, 5), Tuple.of(3, 5)),
				createMap(Tuple.of(1, 4), Tuple.of(3, 4)),
				createMap(Tuple.of(1, 5), Tuple.of(3, 4)),
				createMap(Tuple.of(1, 4), Tuple.of(3, 5)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 5)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 5))
			);
		}

		@SafeVarargs
		private final <T, U> Map<T, U> createMap(Tuple.Tuple2<T, U>... tuples) {
			HashMap<T, U> result = new HashMap<>();
			for (Tuple.Tuple2<T, U> tuple : tuples) {
				result.put(tuple.get1(), tuple.get2());
			}
			return result;
		}

		@Example
		void tooManyCombinations() {
			IntegerArbitrary keys = Arbitraries.integers().between(1, 1000);
			IntegerArbitrary values = Arbitraries.integers().between(1000, 2000);
			Optional<ExhaustiveGenerator<Optional<Map<Integer, Integer>>>> optionalGenerator =
				Arbitraries.maps(keys, values).ofMaxSize(10).optional().exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	@Label("Combinators")
	class CombinatorsTests {

		@Example
		void combine2arbitraries() {
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1020, a12)
										  .as((i1, i2) -> i1 + i2);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(11, 12, 21, 22);
		}

		@Example
		void combine3arbitraries() {
			Arbitrary<Integer> a100200 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a100200, a1020, a12)
										  .as((i1, i2, i3) -> i1 + i2 + i3);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(8);
			assertThat(generator).containsExactly(111, 112, 121, 122, 211, 212, 221, 222);
		}

		@Example
		void combine4arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4)
										  .as((i1, i2, i3, i4) -> i1 + i2 + i3 + i4);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(16);
			assertThat(generator).hasSize(16);
			assertThat(generator).contains(1111, 2222);
		}

		@Example
		void combine5arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5)
										  .as((i1, i2, i3, i4, i5) -> i1 + i2 + i3 + i4 + i5);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(32);
			assertThat(generator).hasSize(32);
			assertThat(generator).contains(11111, 22222);
		}

		@Example
		void combine6arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6)
										  .as((i1, i2, i3, i4, i5, i6) -> i1 + i2 + i3 + i4 + i5 + i6);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(64);
			assertThat(generator).hasSize(64);
			assertThat(generator).contains(111111, 222222);
		}

		@Example
		void combine7arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7)
										  .as((i1, i2, i3, i4, i5, i6, i7) -> i1 + i2 + i3 + i4 + i5 + i6 + i7);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(128);
			assertThat(generator).hasSize(128);
			assertThat(generator).contains(1111111, 2222222);
		}

		@Example
		void combine8arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> a8 = Arbitraries.of(10000000, 20000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7, a8)
										  .as((i1, i2, i3, i4, i5, i6, i7, i8) -> i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(256);
			assertThat(generator).hasSize(256);
			assertThat(generator).contains(11111111, 22222222);
		}

		@Example
		void combineArbitraryList() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> plus = Combinators
										  .combine(asList(a1, a2, a3))
										  .as(params -> params.stream().mapToInt(i -> i).sum());

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(12);
			assertThat(generator).containsOnly(111, 112, 113, 121, 122, 123, 211, 212, 213, 221, 222, 223);
		}

		@Example
		void combineWithBuilder() {
			Arbitrary<Integer> numbers = Arbitraries.integers().between(1, 4);

			Supplier<AdditionBuilder> additionBuilderSupplier = AdditionBuilder::new;
			Arbitrary<Integer> sum = Combinators
										 .withBuilder(additionBuilderSupplier)
										 .use(numbers).in((b, n) -> b.addNumber(n))
										 .use(numbers).in((b, n) -> b.addNumber(n))
										 .build(AdditionBuilder::sum);

			assertThat(sum.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = sum.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(16);
			assertThat(generator).containsOnly(2, 3, 4, 5, 6, 7, 8);
		}
	}

	@Example
	@Label("Arbitraries.constant() returns the constant once")
	void constant() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.constant("abc").exhaustive();
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
			Arbitraries.constant("e")
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
			Tuple.of(3, Arbitraries.constant("e"))
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

		private List<Integer> numbers = new ArrayList<>();

		AdditionBuilder addNumber(int number) {
			numbers.add(number);
			return this;
		}

		int sum() {
			return numbers.stream().mapToInt(n -> n).sum();
		}
	}
}
