package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.ArrayList;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
@Label("Default Edge Cases")
class DefaultEdgeCasesTests {

	@Example
	void mapping() {
		Arbitrary<String> arbitrary = Arbitraries.integers().between(-10, 10).map(i -> Integer.toString(i));
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			"-10", "-2", "-1", "0", "1", "2", "10"
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(7);
	}

	@Example
	@Disabled
	void filtering() {
	}

	@Example
	@Disabled
	void withNull() {
	}

	@Example
	@Disabled
	void fixGenSize() {
	}

	@Example
	@Disabled
	void flatMapping() {
	}

	@Example
	@Disabled
	void unique() {
	}

	@Example
	@Disabled
	@Label("Arbitrary.optional()")
	void optionals() {
	}

	@Example
	@Disabled
	@Label("Arbitraries.map()")
	void maps() {
	}

	@Example
	@Disabled
	@Label("Arbitraries.constant() returns the constant once")
	void constant() {
		Arbitraries.constant("abc");
	}

	@Example
	@Disabled
	@Label("Arbitraries.create() returns the created value once")
	void create() {
		Arbitraries.create(() -> new Object());
	}

	@Example
	@Disabled
	@Label("Arbitraries.shuffle() returns all permutations")
	void shuffle() {
		Arbitraries.shuffle(1, 2, 3);
	}

	@Example
	@Disabled
	@Label("Arbitraries.oneOf()")
	void oneOf() {
		Arbitraries.oneOf(
			Arbitraries.of("a", "b"),
			Arbitraries.of("c", "d"),
			Arbitraries.constant("e")
		);
	}

	@Example
	@Disabled
	@Label("Arbitraries.frequencyOf()")
	void frequencyOf() {
		Arbitraries.frequencyOf(
			Tuple.of(1, Arbitraries.of("a", "b")),
			Tuple.of(2, Arbitraries.of("c", "d")),
			Tuple.of(3, Arbitraries.constant("e"))
		);
	}

	@Group
	@Disabled
	@Label("Arbitraries.strings()|chars()")
	class StringsAndChars {
		@Example
		void singleRangeChars() {
		}


		@Example
		void multiRangeChars() {
		}

		@Example
		void generateAllPossibleStrings() {
			Arbitraries.strings().withChars('a', 'b').ofMinLength(0).ofMaxLength(2);
		}

		@Example
		void allNumberStringsWith5Digits() {
			Arbitraries.strings().numeric().ofLength(5);
		}

	}

	@Group
	@Disabled
	class OfValues {

		@Example
		void booleans() {
		}

		@Example
		void values() {
		}

		@Example
		@Label("Arbitraries.samples() returns all samples in row")
		void samples() {
		}

		@Example
		@Label("Arbitraries.frequency() returns all in row")
		void frequency() {
		}

		@Example
		void enums() {
		}

		@Example
		void withNulls() {
			Arbitraries.of("string1", null, "string3");
		}
	}

	@Group
	class FloatsAndDecimals {

		@Example
		void bigDecimals() {
			int scale = 2;
			BigDecimalArbitrary arbitrary = new DefaultBigDecimalArbitrary()
												.between(BigDecimal.valueOf(-10), BigDecimal.valueOf(10))
												.ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(-10),
				BigDecimal.valueOf(-1),
				BigDecimal.valueOf(-0.01),
				BigDecimal.ZERO.movePointLeft(scale),
				BigDecimal.valueOf(0.01),
				BigDecimal.valueOf(1),
				BigDecimal.valueOf(10)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(7);
		}

		@Example
		void bigDecimalsWithExcludedBorders() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = new DefaultBigDecimalArbitrary()
												.between(BigDecimal.valueOf(-10), false, BigDecimal.valueOf(10), false)
												.ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(-9.9),
				BigDecimal.valueOf(-1),
				BigDecimal.valueOf(-0.1),
				BigDecimal.ZERO.movePointLeft(scale),
				BigDecimal.valueOf(0.1),
				BigDecimal.valueOf(1),
				BigDecimal.valueOf(9.9)
			);
		}

		@Example
		void bigDecimalsWithShrinkingTarget() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = new DefaultBigDecimalArbitrary()
												.between(BigDecimal.valueOf(1), BigDecimal.valueOf(10))
												.ofScale(scale)
												.shrinkTowards(BigDecimal.valueOf(5));
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(1),
				BigDecimal.valueOf(4.9),
				BigDecimal.valueOf(5),
				BigDecimal.valueOf(5.1),
				BigDecimal.valueOf(10)
			);
		}

		@Example
		void doubles() {
			int scale = 2;
			DoubleArbitrary arbitrary = new DefaultDoubleArbitrary()
				.between(-10.0, 10.0)
				.ofScale(scale);
			EdgeCases<Double> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10.0, -1.0, -0.01, 0.0, 0.01, 1.0, 10.0
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(7);
		}

		@Example
		void floats() {
			int scale = 2;
			FloatArbitrary arbitrary = new DefaultFloatArbitrary()
				.between(-10.0f, 10.0f)
				.ofScale(scale);
			EdgeCases<Float> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10.0f, -1.0f, -0.01f, 0.0f, 0.01f, 1.0f, 10.0f
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(7);
		}

	}

	@Group
	@Disabled
	@Label("Combinators")
	class CombinatorsTests {

		@Example
		void combine2arbitraries() {
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1020, a12)
										  .as((i1, i2) -> i1 + i2);
		}

		@Example
		void combine3arbitraries() {
			Arbitrary<Integer> a100200 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a100200, a1020, a12)
										  .as((i1, i2, i3) -> i1 + i2 + i3);
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
		}

		class AdditionBuilder {

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

	@Group
	class Integrals {

		@Example
		void intEdgeCases() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(-10, 10);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, -1, 0, 1, 2, 10
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(7);
		}

		@Example
		void intOnlyPositive() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(5, 100);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 100
			);
		}

		@Example
		void intWithShrinkTarget() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(5, 100).shrinkTowards(42);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 42, 100
			);
		}

		@Example
		void shorts() {
			ShortArbitrary arbitrary = new DefaultShortArbitrary().between((short) -5, (short) 5);
			EdgeCases<Short> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(short) -5, (short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 5
			);
		}

		@Example
		void bytes() {
			ByteArbitrary arbitrary = new DefaultByteArbitrary().between((byte) -5, (byte) 5);
			EdgeCases<Byte> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(byte) -5, (byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 5
			);
		}

		@Example
		void longs() {
			LongArbitrary arbitrary = new DefaultLongArbitrary().between(-5, 5);
			EdgeCases<Long> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(long) -5, (long) -2, (long) -1, (long) 0, (long) 1, (long) 2, (long) 5
			);
		}

		@Example
		void bigIntegers() {
			BigIntegerArbitrary arbitrary = new DefaultBigIntegerArbitrary().between(BigInteger.valueOf(-5), BigInteger.valueOf(5));
			EdgeCases<BigInteger> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigInteger.valueOf(-5),
				BigInteger.valueOf(-2),
				BigInteger.valueOf(-1),
				BigInteger.valueOf(0),
				BigInteger.valueOf(1),
				BigInteger.valueOf(2),
				BigInteger.valueOf(5)
			);
		}

	}

	@Group
	class CollectionTypes {

		@Example
		void listEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void listEdgeCasesWhenMinSize1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
		}

		@Example
		void listEdgeCasesWhenMinSizeGreaterThan1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(2);
			assertThat(values(arbitrary.edgeCases())).isEmpty();
		}

		@Example
		void listEdgeCasesAreGeneratedFreshlyOnEachCallToIterator() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-1, 1);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			EdgeCases<List<Integer>> edgeCases = arbitrary.edgeCases();

			for (Shrinkable<List<Integer>> listShrinkable : edgeCases) {
				listShrinkable.value().add(42);
			}

			Set<List<Integer>> values = values(edgeCases);
			assertThat(values).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1)
			);
		}

		@Example
		void setEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptySet(),
				Collections.singleton(-10),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singleton(-10),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(10)
			);
		}

		@Example
		void streamEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			Set<Stream<Integer>> streams = values(arbitrary.edgeCases());
			Set<List<Integer>> lists = streams.stream().map(stream -> stream.collect(Collectors.toList())).collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void iteratorEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Iterator<Integer>> arbitrary = ints.iterator();
			Set<Iterator<Integer>> iterators = values(arbitrary.edgeCases());
			Set<List<Integer>> lists =
				iterators.stream()
						 .map(iterator -> {
							 List<Integer> list = new ArrayList<>();
							 while (iterator.hasNext()) { list.add(iterator.next()); }
							 return list;
						 })
						 .collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			StreamableArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				new Integer[]{},
				new Integer[]{-10},
				new Integer[]{-2},
				new Integer[]{-1},
				new Integer[]{0},
				new Integer[]{1},
				new Integer[]{2},
				new Integer[]{10}
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

	}

	private <T> Set<T> values(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}
}
