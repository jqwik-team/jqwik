package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static org.assertj.core.api.Assertions.*;

@Group
class ActionGeneratorTests {

	private static final Action<Integer> PLUS_2 = new Action<Integer>() {
		@Override
		public Integer run(Integer anInt) {
			return anInt + 2;
		}

		@Override
		public String toString() {
			return "+2";
		}
	};

	private static final Action<Integer> PLUS_1 = new Action<Integer>() {
		@Override
		public Integer run(Integer anInt) {
			return anInt + 2;
		}

		@Override
		public String toString() {
			return "+2";
		}
	};

	@Group
	class RandomGenerator {

		@Example
		void generatesActionsFromArbitrary(@ForAll JqwikRandom random) {
			Arbitrary<Action<Integer>> samples = new OrderedArbitraryForTesting<>(plus1(), plus2());

			RandomActionGenerator<Integer> actionGenerator = new RandomActionGenerator<>(samples, 1000, random);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);

			assertThat(actionGenerator.generated()).hasSize(6);
		}

		@Example
		void ignoresActionsWithFailingPrecondition(@ForAll JqwikRandom random) {
			Arbitrary<Action<Integer>> samples = new OrderedArbitraryForTesting<>(plus1(), plus2(), failedPrecondition());

			RandomActionGenerator<Integer> actionGenerator = new RandomActionGenerator<>(samples, 1000, random);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);

			assertThat(actionGenerator.generated()).hasSize(4);
		}

		@Example
		void stopsSearchingForActionsAfter1000Tries(@ForAll JqwikRandom random) {
			Arbitrary<Action<Integer>> samples = Arbitraries.of(failedPrecondition());

			RandomActionGenerator<Integer> actionGenerator = new RandomActionGenerator<>(samples, 1000, random);

			assertThatThrownBy(() -> actionGenerator.next(42)).isInstanceOf(NoSuchElementException.class);
			assertThat(actionGenerator.generated()).hasSize(0);
		}
	}

	@Group
	class FromShrinkables {
		@Example
		void generateActionsFromListOfShrinkables() {
			List<Shrinkable<Action<Integer>>> shrinkables = Arrays.asList(
				Shrinkable.unshrinkable(PLUS_1),
				Shrinkable.unshrinkable(PLUS_2),
				Shrinkable.unshrinkable(PLUS_1)
			);

			ShrinkablesActionGenerator<Integer> actionGenerator = new ShrinkablesActionGenerator<>(shrinkables);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.generated()).hasSize(3);

			assertThatThrownBy(() -> actionGenerator.next(42)).isInstanceOf(NoSuchElementException.class);
		}

		@Example
		void filterOutFailingPreconditions() {
			List<Shrinkable<Action<Integer>>> shrinkables = Arrays.asList(
				Shrinkable.unshrinkable(PLUS_1),
				Shrinkable.unshrinkable(PLUS_2),
				Shrinkable.unshrinkable(failedPrecondition()),
				Shrinkable.unshrinkable(PLUS_1)
			);

			ShrinkablesActionGenerator<Integer> actionGenerator = new ShrinkablesActionGenerator<>(shrinkables);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.generated()).hasSize(3);
		}
	}

	private Action<Integer> plus1() {
		return PLUS_1;
	}

	private Action<Integer> plus2() {
		return PLUS_2;
	}

	private Action<Integer> failedPrecondition() {
		return new Action<Integer>() {
			@Override
			public boolean precondition(Integer state) {
				return false;
			}

			@Override
			public Integer run(Integer anInt) {
				return anInt + 100;
			}

			@Override
			public String toString() {
				return "failedPrecondition";
			}
		};
	}

}
