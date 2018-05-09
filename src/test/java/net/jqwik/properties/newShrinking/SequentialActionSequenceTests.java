package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;

import java.util.*;

class SequentialActionSequenceTests {


	@Example
	void runSequence() {
		NSequentialActionSequence<Integer> sequence = createSequence( //
			plus1(), //
			ignore(), //
			plus10(), //
			ignore(), //
			plus100() //
		);

		System.out.println(sequence);

		int result = sequence.run(0);
		Assertions.assertThat(result).isEqualTo(111);

		System.out.println(sequence);
	}

	@Example
	void runWithFailure() {
		NSequentialActionSequence<Integer> sequence = createSequence( //
			plus1(), //
			ignore(), //
			plus10(), //
			ignore(), //
			fail(), plus100() //
		);

		Assertions.assertThatThrownBy(() -> sequence.run(0)).isInstanceOf(AssertionError.class);
	}

	private Action<Integer> fail() {
		return new Action<Integer>() {
			@Override
			public Integer run(Integer model) {
				Assertions.assertThat(model).isEqualTo(42);
				return model;
			}

			@Override
			public String toString() {
				return "check 42";
			}
		};
	}

	private Action<Integer> ignore() {
		return new Action<Integer>() {
			@Override
			public boolean precondition(Integer model) {
				return false;
			}

			@Override
			public Integer run(Integer model) {
				return null;
			}

			@Override
			public String toString() {
				return "ignore";
			}
		};
	}

	private Action<Integer> plus1() {
		return new Action<Integer>() {
			@Override
			public Integer run(Integer anInt) {
				return anInt + 1;
			}

			@Override
			public String toString() {
				return "+1";
			}
		};
	}

	private Action<Integer> plus10() {
		return new Action<Integer>() {
			@Override
			public Integer run(Integer anInt) {
				return anInt + 10;
			}

			@Override
			public String toString() {
				return "+10";
			}
		};
	}

	private Action<Integer> plus100() {
		return new Action<Integer>() {
			@Override
			public Integer run(Integer anInt) {
				return anInt + 100;
			}

			@Override
			public String toString() {
				return "+100";
			}
		};
	}

	private NSequentialActionSequence<Integer> createSequence(Action<Integer>... actions) {
		List<Action<Integer>> list = Arrays.asList(actions);
		return new NSequentialActionSequence<>(list);
	}

}
