package net.jqwik.api.edgeCases;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class ArbitrariesEdgeCasesTests implements GenericEdgeCasesProperties {

	@Override
	public Arbitrary<Arbitrary<?>> arbitraries() {
		return Arbitraries.of(
			entriesArbitrary(),
			Arbitraries.just("abc"),
			Arbitraries.create(() -> "new string"),
			Arbitraries.shuffle(1, 2, 3),
			oneOfArbitrary(),
			frequencyOfArbitrary()
		);
	}

	enum MyEnum {FIRST, B, C, LAST}

	@Example
	@Label("Arbitraries.entries(key, value)")
	void entries() {
		Arbitrary<Map.Entry<String, Integer>> arbitrary = entriesArbitrary();
		EdgeCases<Map.Entry<String, Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				mapEntry("a", 10),
				mapEntry("a", 100),
				mapEntry("z", 10),
				mapEntry("z", 100)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
	}

	private Arbitrary<Map.Entry<String, Integer>> entriesArbitrary() {
		StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
		Arbitrary<Integer> values = Arbitraries.of(10, 100);
		Arbitrary<Map.Entry<String, Integer>> arbitrary = Arbitraries.entries(keys, values);
		return arbitrary;
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
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactly("abc");
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.create(supplier)")
	void create() {
		Arbitrary<String> arbitrary = Arbitraries.create(() -> "new string");
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactly("new string");
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.defaultFor()")
	void defaultFor() {
		Arbitrary<Integer> arbitrary = Arbitraries.defaultFor(Integer.class);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
			-2, -1, 0, 1, 2,
			Integer.MIN_VALUE, Integer.MAX_VALUE,
			Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(9);
	}

	@Example
	@Label("Arbitraries.shuffle()")
	void shuffle() {
		Arbitrary<List<Integer>> arbitrary = Arbitraries.shuffle(1, 2, 3);
		EdgeCases<List<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactly(asList(1, 2, 3));
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(1);
	}

	@Example
	@Label("Arbitraries.oneOf(values)")
	void oneOf() {
		Arbitrary<Integer> arbitrary = oneOfArbitrary();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-1, 0, 1, 100, 10000
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(5);
	}

	private Arbitrary<Integer> oneOfArbitrary() {
		return Arbitraries.oneOf(
				Arbitraries.integers().between(-1, 1),
				Arbitraries.of(100, 10000)
		);
	}

	@Example
	@Label("Arbitraries.frequencyOf(tuples)")
	void frequencyOf() {
		Arbitrary<Integer> arbitrary = frequencyOfArbitrary();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-1, 0, 1, 100, 101, Integer.MAX_VALUE - 1, Integer.MAX_VALUE
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(7);
	}

	private Arbitrary<Integer> frequencyOfArbitrary() {
		return Arbitraries.frequencyOf(
			Tuple.of(1, Arbitraries.integers().between(-1, 1)),
			Tuple.of(2, Arbitraries.integers().greaterOrEqual(100))
		);
	}

	@Example
	@Label("Arbitraries.frequencyOf(tuples)")
	void frequencyOfWithZeroFrequency() {
		Arbitrary<Integer> arbitrary = Arbitraries.frequencyOf(
				Tuple.of(0, Arbitraries.integers().between(-1, 1)),
				Tuple.of(2, Arbitraries.integers().greaterOrEqual(100))
		);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				100, 101, Integer.MAX_VALUE - 1, Integer.MAX_VALUE
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
	}

	@Group
	class OfValues implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.of(4, 9, 2, 1, 66, 2),
				frequencyArbitrary(),
				Arbitraries.of(MyEnum.class)
			);
		}

		@Example
		@Label("Arbitraries.of(true, false)")
		void ofBooleans() {
			Arbitrary<Boolean> arbitrary = Arbitraries.of(true, false);
			EdgeCases<Boolean> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder(true, false);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		@Example
		@Label("Arbitraries.of(...)")
		void ofValues() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(4, 9, 2, 1, 66, 2);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder(4, 2);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		@Example
		void ofValuesWithNulls() {
			Arbitrary<String> arbitrary = Arbitraries.of("first", "other", null, "last");
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder("first", "last", null);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(3);
		}

		@Example
		@Label("Arbitraries.of(char[] chars)")
		void ofChars() {
			char[] chars = {'x', 'a', 'b', 'c'};
			Arbitrary<Character> arbitrary = Arbitraries.of(chars);
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder('x', 'c');
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		@Example
		@Label("Arbitraries.frequency()")
		void frequency() {
			Arbitrary<Integer> arbitrary = frequencyArbitrary();
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder(4, 2);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		private Arbitrary<Integer> frequencyArbitrary() {
			Arbitrary<Integer> arbitrary = Arbitraries.frequency(
					Tuple.of(1, 4),
					Tuple.of(1, 9),
					Tuple.of(1, 2),
					Tuple.of(1, 1),
					Tuple.of(1, 66),
					Tuple.of(1, 2)
			);
			return arbitrary;
		}

		@Example
		void enums() {
			Arbitrary<MyEnum> arbitrary = Arbitraries.of(MyEnum.class);
			EdgeCases<MyEnum> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
					.containsExactlyInAnyOrder(MyEnum.FIRST, MyEnum.LAST);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}
	}

	@Group
	@Label("Arbitraries.strings()|chars()")
	class StringsAndChars implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.strings(),
				Arbitraries.chars()
			);
		}

		@Example
		void singleRangeChars() {
			CharacterArbitrary arbitrary = Arbitraries.chars().range('a', 'z');
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					'a', 'z'
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		@Example
		void multiRangeChars() {
			CharacterArbitrary arbitrary = Arbitraries.chars().range('a', 'z').numeric();
			EdgeCases<Character> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					'a', 'z', '0', '9'
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

		@Example
		void strings() {
			StringArbitrary arbitrary = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(0);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					"", "a", "z"
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(3);
		}

		@Property
		void stringsOfFixedLength(@ForAll @IntRange(min = 0, max = 10) int stringLength) {
			StringArbitrary arbitrary = Arbitraries.strings().withCharRange('a', 'z').ofLength(stringLength);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			if (stringLength == 0) {
				assertThat(collectEdgeCaseValues(edgeCases)).containsExactly("");
			} else {
				assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
				assertThat(collectEdgeCaseValues(edgeCases)).allMatch(aString -> aString.length() == stringLength);
				assertThat(collectEdgeCaseValues(edgeCases)).allMatch(aString -> aString.chars().allMatch(c -> c == 'a' || c == 'z'));
			}
		}

		@Property
		void stringsWithMinLength(@ForAll @IntRange(min = 2, max = 10) int minLength) {
			StringArbitrary arbitrary = Arbitraries.strings().withCharRange('a', 'z')
												   .ofMinLength(minLength)
												   .ofMaxLength(20);
			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
			assertThat(collectEdgeCaseValues(edgeCases)).allMatch(aString -> aString.length() == minLength);
			assertThat(collectEdgeCaseValues(edgeCases)).allMatch(aString -> aString.chars().allMatch(c -> c == 'a' || c == 'z'));
		}

	}

}
