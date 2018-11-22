package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static org.assertj.core.api.Assertions.*;

@Group
class NActionGeneratorTests {

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
		void generatesActionsFromArbitrary(@ForAll Random random) {
			Arbitrary<Action<Integer>> samples = Arbitraries.samples(plus1(), plus2());

			NRandomActionGenerator<Integer> actionGenerator = new NRandomActionGenerator<>(samples, 1000, random);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);

			assertThat(actionGenerator.shrinkableActions()).hasSize(6);
		}

		@Example
		void ignoresActionsWithFailingPrecondition(@ForAll Random random) {
			Arbitrary<Action<Integer>> samples = Arbitraries.samples(plus1(), plus2(), failedPrecondition());

			NRandomActionGenerator<Integer> actionGenerator = new NRandomActionGenerator<>(samples, 1000, random);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);

			assertThat(actionGenerator.shrinkableActions()).hasSize(4);
		}
	}

	@Group
	class FromShrinkables {
		@Example
		void generateActionsFromListOfShrinkables(@ForAll Random random) {
			List<Shrinkable<Action<Integer>>> shrinkables = Arrays.asList(
				Shrinkable.unshrinkable(PLUS_1),
				Shrinkable.unshrinkable(PLUS_2),
				Shrinkable.unshrinkable(PLUS_1)
			);

			NShrinkablesActionGenerator<Integer> actionGenerator = new NShrinkablesActionGenerator<>(shrinkables);

			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_2);
			assertThat(actionGenerator.next(42)).isEqualTo(PLUS_1);

			assertThatThrownBy(() -> actionGenerator.next(42)).isInstanceOf(NoSuchElementException.class);
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
			public boolean precondition(Integer model) {
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
