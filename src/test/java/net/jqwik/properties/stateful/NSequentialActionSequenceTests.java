package net.jqwik.properties.stateful;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class NSequentialActionSequenceTests {


	@Example
	void run() {
		NSequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			square()
		);

		int result = sequence.run(0);
		Assertions.assertThat(result).isEqualTo(121);
		Assertions.assertThat(result).isEqualTo(sequence.state());
		Assertions.assertThat(sequence.runSequence()).hasSize(3);
	}

	@Example
	void wontRunTwice() {
		NSequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			square()
		);

		sequence.run(0);
		int result = sequence.run(0);
		Assertions.assertThat(result).isEqualTo(121);
		Assertions.assertThat(result).isEqualTo(sequence.state());
		Assertions.assertThat(sequence.runSequence()).hasSize(3);
	}

	@Example
	void runWithFailure() {
		NSequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			check42(),
			square()
		);

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(AssertionError.class);

		Assertions.assertThat(sequence.state()).isEqualTo(11);
		Assertions.assertThat(sequence.runSequence()).hasSize(3);
	}

	@Example
	void failInInvariant() {
		ActionSequence<Integer> sequence = createSequence(
			plus10(),
			square(),
			plus10()
		).withInvariant(anInt -> Assertions.assertThat(anInt).isLessThan(100));

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(AssertionError.class);

		Assertions.assertThat(sequence.state()).isEqualTo(100);
		Assertions.assertThat(sequence.runSequence()).hasSize(2);
	}

	private Function<Integer, Action<Integer>> check42() {
		return ignore -> new Action<Integer>() {
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

	private Function<Integer, Action<Integer>> ignore() {
		return ignore -> new Action<Integer>() {
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

	private Function<Integer, Action<Integer>> plus1() {
		return ignore -> new Action<Integer>() {
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

	private Function<Integer, Action<Integer>> plus10() {
		return ignore -> new Action<Integer>() {
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

	private Function<Integer, Action<Integer>> square() {
		return number -> new Action<Integer>() {
			@Override
			public Integer run(Integer anInt) {
				return anInt * number; // anInt should be number
			}

			@Override
			public String toString() {
				return "^2";
			}
		};
	}

	@SuppressWarnings("unchecked")
	private NSequentialActionSequence<Integer> createSequence(Function<Integer, Action<Integer>>... actions) {
		Iterator<Function<Integer, Action<Integer>>> iterator = Arrays.asList(actions).iterator();
		NActionGenerator<Integer> actionGenerator = model -> {
			if (iterator.hasNext())
				return iterator.next().apply(model);
			throw new NoSuchElementException("No more actions available");
		};
		return new NSequentialActionSequence<>(actionGenerator, actions.length);
	}

}
