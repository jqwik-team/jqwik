package net.jqwik.api.edgeCases;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
class ArbitrariesEdgeCasesTests {

	enum MyEnum {FIRST, B, C, LAST}

	@Example
	@Label("Arbitraries.map(key, value)")
	void maps() {
		StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
		Arbitrary<Integer> values = Arbitraries.of(10, 100);
		Arbitrary<Map<String, Integer>> arbitrary = Arbitraries.maps(keys, values);
		EdgeCases<Map<String, Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			Collections.emptyMap(),
			Collections.singletonMap("a", 10),
			Collections.singletonMap("a", 100),
			Collections.singletonMap("z", 10),
			Collections.singletonMap("z", 100)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(5);
	}

	@Example
	@Label("Arbitraries.entries(key, value)")
	void entries() {
		StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
		Arbitrary<Integer> values = Arbitraries.of(10, 100);
		Arbitrary<Map.Entry<String, Integer>> arbitrary = Arbitraries.entries(keys, values);
		EdgeCases<Map.Entry<String, Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			mapEntry("a", 10),
			mapEntry("a", 100),
			mapEntry("z", 10),
			mapEntry("z", 100)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(4);
	}

	private <K, V> Map.Entry<K, V> mapEntry(K key, V value) {
		return new Map.Entry<K, V>() {
			@Override
			public K getKey() {
				return key;
			}

			@Override
			public V getValue() {
				return value;
			}

			@Override
			public V setValue(final V value) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Example
	@Label("Arbitraries.constant(value)")
	void constant() {
		Arbitrary<String> arbitrary = Arbitraries.just("abc");
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactly("abc");
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.create(supplier)")
	void create() {
		Arbitrary<String> arbitrary = Arbitraries.create(() -> "new string");
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactly("new string");
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.recursive()")
	void recursive() {
		Arbitrary<Integer> base = Arbitraries.of(5, 10);
		Arbitrary<Integer> arbitrary = Arbitraries.recursive(() -> base, list -> list.map(i -> i + 1), 3);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactly(8, 13);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(2);
	}

	@Example
	@Label("Arbitraries.shuffle()")
	void shuffle() {
		Arbitrary<List<Integer>> arbitrary = Arbitraries.shuffle(1, 2, 3);
		EdgeCases<List<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactly(asList(1, 2, 3));
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.oneOf(values)")
	void oneOf() {
		Arbitrary<Integer> arbitrary = Arbitraries.oneOf(
			Arbitraries.integers().between(-1, 1),
			Arbitraries.of(100, 10000)
		);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			-1, 0, 1, 100, 10000
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(5);
	}

	@Example
	@Label("Arbitraries.frequencyOf(tuples)")
	void frequencyOf() {
		Arbitrary<Integer> arbitrary = Arbitraries.frequencyOf(
			Tuple.of(1, Arbitraries.integers().between(-1, 1)),
			Tuple.of(2, Arbitraries.integers().greaterOrEqual(100))
		);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			-1, 0, 1, 100, 101, Integer.MAX_VALUE - 1, Integer.MAX_VALUE
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(7);
	}

	@Example
	@Label("Arbitraries.frequencyOf(tuples)")
	void frequencyOfWithZeroFrequency() {
		Arbitrary<Integer> arbitrary = Arbitraries.frequencyOf(
			Tuple.of(0, Arbitraries.integers().between(-1, 1)),
			Tuple.of(2, Arbitraries.integers().greaterOrEqual(100))
		);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			100, 101, Integer.MAX_VALUE - 1, Integer.MAX_VALUE
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(4);
	}

	@Example
	@Label("Functions.function(type, returnArbitrary)")
	void functionHasConstantFunctionsAsEdgeCases() {
		Arbitrary<Integer> integers = Arbitraries.integers().between(10, 100);
		Arbitrary<Function<String, Integer>> arbitrary =
			Functions.function(Function.class).returns(integers);

		EdgeCases<Function<String, Integer>> edgeCases = arbitrary.edgeCases();
		Set<Function<String, Integer>> functions = values(edgeCases);
		assertThat(functions).hasSize(4);

		for (Function<String, Integer> function : functions) {
			assertThat(function.apply("any string")).isIn(10, 11, 99, 100);
		}

		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(4);
	}

	@Group
	class OfValues {

		@Example
		@Label("Arbitraries.of(true, false)")
		void ofBooleans() {
			Arbitrary<Boolean> arbitrary = Arbitraries.of(true, false);
			EdgeCases<Boolean> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder(true, false);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}

		@Example
		@Label("Arbitraries.of(...)")
		void ofValues() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(4, 9, 2, 1, 66, 2);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder(4, 2);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}

		@Example
		void ofValuesWithNulls() {
			Arbitrary<String> arbitrary = Arbitraries.of("first", "other", null, "last");
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder("first", "last", null);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(3);
		}

		@Example
		@Label("Arbitraries.of(char[] chars)")
		void ofChars() {
			char[] chars = {'x', 'a', 'b', 'c'};
			Arbitrary<Character> arbitrary = Arbitraries.of(chars);
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder('x', 'c');
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}

		@Example
		@Label("Arbitraries.frequency()")
		void frequency() {
			Arbitrary<Integer> arbitrary = Arbitraries.frequency(
				Tuple.of(1, 4),
				Tuple.of(1, 9),
				Tuple.of(1, 2),
				Tuple.of(1, 1),
				Tuple.of(1, 66),
				Tuple.of(1, 2)
			);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder(4, 2);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}

		@Example
		void enums() {
			Arbitrary<MyEnum> arbitrary = Arbitraries.of(MyEnum.class);
			EdgeCases<MyEnum> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases))
				.containsExactlyInAnyOrder(MyEnum.FIRST, MyEnum.LAST);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}
	}

	@Group
	@Label("Arbitraries.strings()|chars()")
	class StringsAndChars {
		@Example
		void singleRangeChars() {
			CharacterArbitrary arbitrary = Arbitraries.chars().range('a', 'z');
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				'a', 'z'
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(2);
		}

		@Example
		void multiRangeChars() {
			CharacterArbitrary arbitrary = Arbitraries.chars().range('a', 'z').digit();
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				'a', 'z', '0', '9'
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(4);
		}

		@Example
		void strings() {
			StringArbitrary arbitrary = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(0);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				"", "a", "z"
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(3);
		}

		@Property
		void stringsOfFixedLength(@ForAll @IntRange(min = 0, max = 10) int stringLength) {
			StringArbitrary arbitrary = Arbitraries.strings().withCharRange('a', 'z').ofLength(stringLength);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			if (stringLength == 0) {
				assertThat(values(edgeCases)).containsExactly("");
			} else {
				assertThat(values(edgeCases)).hasSize(2);
				assertThat(values(edgeCases)).allMatch(aString -> aString.length() == stringLength);
				assertThat(values(edgeCases)).allMatch(aString -> aString.chars().allMatch(c -> c == 'a' || c == 'z'));
			}
		}

		@Property
		void stringsOfFixedLengthWithUniqueCharacters(@ForAll @IntRange(min = 2, max = 10) int stringLength) {
			Arbitrary<Character> uniqueCharacters = Arbitraries.chars().range('a', 'z').unique();
			StringArbitrary arbitrary = Arbitraries.strings().withChars(uniqueCharacters).ofLength(stringLength);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).isEmpty();
		}

	}

	@Group
	class FloatsAndDecimals {

		@Example
		void bigDecimals() {
			int scale = 2;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(-10), BigDecimal.valueOf(10))
													   .ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(-10).setScale(2),
				BigDecimal.valueOf(-1).setScale(2),
				BigDecimal.valueOf(-0.01),
				BigDecimal.ZERO.setScale(2),
				BigDecimal.valueOf(0.01),
				BigDecimal.valueOf(1).setScale(2),
				BigDecimal.valueOf(10).setScale(2)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(7);
		}

		@Example
		void bigDecimalsWithExcludedBorders() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(-10), false, BigDecimal.valueOf(10), false)
													   .ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(-9.9),
				BigDecimal.valueOf(-1).setScale(1),
				BigDecimal.valueOf(-0.1),
				BigDecimal.ZERO.setScale(1),
				BigDecimal.valueOf(0.1),
				BigDecimal.valueOf(1).setScale(1),
				BigDecimal.valueOf(9.9)
			);
		}

		@Example
		void bigDecimalsWithShrinkingTarget() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(1), BigDecimal.valueOf(10))
													   .ofScale(scale)
													   .shrinkTowards(BigDecimal.valueOf(5));
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigDecimal.valueOf(1).setScale(1),
				BigDecimal.valueOf(4.9),
				BigDecimal.valueOf(5).setScale(1),
				BigDecimal.valueOf(5.1),
				BigDecimal.valueOf(10).setScale(1)
			);
		}

		@Example
		void doubles() {
			int scale = 2;
			DoubleArbitrary arbitrary = Arbitraries.doubles()
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
			FloatArbitrary arbitrary = Arbitraries.floats()
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
	class Integrals {

		@Example
		void intEdgeCases() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(-10, 10);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).hasSize(9);
		}

		@Example
		void intOnlyPositive() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(5, 100);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 6, 99, 100
			);
		}

		@Example
		void intWithShrinkTarget() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(5, 100).shrinkTowards(42);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 6, 42, 99, 100
			);
		}

		@Example
		void shorts() {
			ShortArbitrary arbitrary = Arbitraries.shorts().between((short) -5, (short) 5);
			EdgeCases<Short> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(short) -5, (short) -4, (short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 4, (short) 5
			);
		}

		@Example
		void bytes() {
			ByteArbitrary arbitrary = Arbitraries.bytes().between((byte) -5, (byte) 5);
			EdgeCases<Byte> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(byte) -5, (byte) -4, (byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 4, (byte) 5
			);
		}

		@Example
		void longs() {
			LongArbitrary arbitrary = Arbitraries.longs().between(-5, 5);
			EdgeCases<Long> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				(long) -5, (long) -4, (long) -2, (long) -1, (long) 0, (long) 1, (long) 2, (long) 4, (long) 5
			);
		}

		@Example
		void bigIntegers() {
			BigIntegerArbitrary arbitrary = Arbitraries.bigIntegers().between(BigInteger.valueOf(-5), BigInteger.valueOf(5));
			EdgeCases<BigInteger> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				BigInteger.valueOf(-5),
				BigInteger.valueOf(-4),
				BigInteger.valueOf(-2),
				BigInteger.valueOf(-1),
				BigInteger.valueOf(0),
				BigInteger.valueOf(1),
				BigInteger.valueOf(2),
				BigInteger.valueOf(4),
				BigInteger.valueOf(5)
			);
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
