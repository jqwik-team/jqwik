package net.jqwik.properties.stateful;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static org.assertj.core.api.Assertions.*;

class NSequentialActionSequenceTests {


	@Example
	void run() {
		NSequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			square()
		);


		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.NOT_RUN);
		int result = sequence.run(1);
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);

		assertThat(result).isEqualTo(144);
		assertThat(result).isEqualTo(sequence.state());
		assertThat(sequence.runSequence()).hasSize(3);
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
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		assertThat(result).isEqualTo(121);
		assertThat(result).isEqualTo(sequence.state());
		assertThat(sequence.runSequence()).hasSize(3);
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

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.FAILED);
		assertThat(sequence.state()).isEqualTo(11);
		assertThat(sequence.runSequence()).hasSize(3);
	}

	@Example
	void stopSequenceIfGeneratorThrowsNoSuchElementException() {
		NSequentialActionSequence<Integer> sequence = createSequence(10,
			plus10(),
			square(),
			plus10()
		);

		sequence.run(1);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		assertThat(sequence.state()).isEqualTo(131);
		assertThat(sequence.runSequence()).hasSize(3);
	}

	@Example
	void failInInvariant() {
		ActionSequence<Integer> sequence = createSequence(
			plus10(),
			square(),
			plus10()
		).withInvariant(anInt -> assertThat(anInt).isLessThan(100));

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(AssertionError.class);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.FAILED);
		assertThat(sequence.state()).isEqualTo(100);
		assertThat(sequence.runSequence()).hasSize(2);
	}

	private Function<Integer, Action<Integer>> preconditionBelow10() {
		return ignore -> new Action<Integer>() {
			@Override
			public boolean precondition(Integer model) {
				return model < 10;
			}

			@Override
			public Integer run(Integer model) {
				return model;
			}

			@Override
			public String toString() {
				return "precondition: < 10";
			}
		};
	}

	private Function<Integer, Action<Integer>> check42() {
		return ignore -> new Action<Integer>() {
			@Override
			public Integer run(Integer model) {
				assertThat(model).isEqualTo(42);
				return model;
			}

			@Override
			public String toString() {
				return "check 42";
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
		return createSequence(actions.length, actions);
	}

	@SuppressWarnings("unchecked")
	private NSequentialActionSequence<Integer> createSequence(int size, Function<Integer, Action<Integer>>... actions) {
		Iterator<Function<Integer, Action<Integer>>> iterator = Arrays.asList(actions).iterator();
		NActionGenerator<Integer> actionGenerator = new NActionGenerator<Integer>() {
			@Override
			public Action<Integer> next(Integer model) {
				if (iterator.hasNext())
					return iterator.next().apply(model);
				throw new NoSuchElementException("No more actions available");
			}

			@Override
			public List<Shrinkable<Action<Integer>>> generated() {
				// Not used here
				return null;
			}
		};
		return new NSequentialActionSequence<>(actionGenerator, size);
	}

}
