package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
@Label("Default Edge Cases")
class DefaultEdgeCasesTests {

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
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, -1, 0, 1, 2, 10
			);
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
