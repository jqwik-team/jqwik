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

	@Example
	void mapping() {
		Optional<ExhaustiveGenerator<String>> optionalGenerator = Arbitraries.integers().between(-5, 5).map(i -> Integer.toString(i)).exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<String> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11);
		assertThat(generator).containsExactly("-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5");
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
}
