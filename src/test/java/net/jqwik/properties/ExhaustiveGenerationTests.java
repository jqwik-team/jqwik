package net.jqwik.properties;

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
	class Integrals {

		@Example
		void integersFromMinToMax() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-10, 10).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(21);
			assertThat(generator).containsExactly(-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

		@Example
		void integerRangeTooBig() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-1, Integer.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}
}
