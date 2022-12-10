package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.assertj.core.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static org.assertj.core.api.Assertions.*;

class SequentialActionSequenceTests {

	@Example
	void run() {
		SequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			square()
		);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.NOT_RUN);
		int result = sequence.run(1);
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);

		assertThat(result).isEqualTo(144);
		assertThat(result).isEqualTo(sequence.finalModel());
		assertThat(sequence.runActions()).hasSize(3);
	}

	@Example
	void runFailsWhenAllActionsHaveFailingPreconditions(@ForAll JqwikRandom random) {
		Action<Integer> actionWithFailingPrecondition = new Action<Integer>() {
			@Override
			public boolean precondition(final Integer state) {
				return false;
			}

			@Override
			public Integer run(final Integer model) {
				return model;
			}
		};

		Arbitrary<ActionSequence<Integer>> arbitrary = Arbitraries.sequences(Arbitraries.just(actionWithFailingPrecondition));
		Shrinkable<ActionSequence<Integer>> shrinkable = arbitrary.generator(1000, true).next(random);
		ActionSequence<Integer> sequence = shrinkable.value();

		assertThatThrownBy(() -> sequence.run(42)).isInstanceOf(JqwikException.class);
	}


	@Example
	void runThrowsExceptionIfNotAtLeastOneActionCanBeGenerated() {
		SequentialActionSequence<Integer> sequence = createSequence(10);
		assertThatThrownBy(() -> sequence.run(42)).isInstanceOf(JqwikException.class);
	}

	@Example
	void canRunAgainWithDifferentModel() {
		SequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			square()
		);

		sequence.run(0);
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		int result = sequence.run(1);
		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		assertThat(result).isEqualTo(144);
		assertThat(result).isEqualTo(sequence.finalModel());
		assertThat(sequence.runActions()).hasSize(3);
	}

	@Example
	void runWithFailure() {
		SequentialActionSequence<Integer> sequence = createSequence(
			plus1(),
			plus10(),
			check42(),
			square()
		);

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(AssertionError.class);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.FAILED);
		assertThat(sequence.finalModel()).isEqualTo(11);
		assertThat(sequence.runActions()).hasSize(3);
	}

	@Example
	void stopSequenceIfGeneratorThrowsNoSuchElementException() {
		SequentialActionSequence<Integer> sequence = createSequence(
			10,
			plus10(),
			square(),
			plus10()
		);

		sequence.run(1);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.SUCCEEDED);
		assertThat(sequence.finalModel()).isEqualTo(131);
		assertThat(sequence.runActions()).hasSize(3);
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
		).isInstanceOf(InvariantFailedError.class);

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.FAILED);
		assertThat(sequence.finalModel()).isEqualTo(100);
		assertThat(sequence.runActions()).hasSize(2);
	}

	@Example
	void failInvariantWithLabel() {
		ActionSequence<Integer> sequence = createSequence(
			plus10(),
			square(),
			plus10()
		).withInvariant("my invariant", anInt -> assertThat(anInt).isLessThan(100));

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(InvariantFailedError.class)
				  .hasMessageContaining("my invariant");

		assertThat(sequence.runState()).isEqualTo(ActionSequence.RunState.FAILED);
		assertThat(sequence.finalModel()).isEqualTo(100);
		assertThat(sequence.runActions()).hasSize(2);
	}

	@Example
	void peekModel() {
		AtomicInteger countPeeks = new AtomicInteger(0);

		ActionSequence<Integer> sequence = createSequence(
			plus10(),
			square(),
			plus10()
		).peek(state -> {
			assertThat(state).isIn(10, 100, 110);
			countPeeks.incrementAndGet();
		});

		sequence.run(0);
		assertThat(countPeeks).hasValue(3);
	}

	@Example
	void exceptionWhilePeeping() {
		ActionSequence<Integer> sequence = createSequence(
			plus10(),
			square(),
			plus10()
		).peek(state -> {
			throw new RuntimeException("oops");
		});

		Assertions.assertThatThrownBy(
			() -> sequence.run(0)
		).isInstanceOf(AssertionFailedError.class);
	}

	private Function<Integer, Action<Integer>> preconditionBelow10() {
		return ignore -> new Action<Integer>() {
			@Override
			public boolean precondition(Integer state) {
				return state < 10;
			}

			@Override
			public Integer run(Integer state) {
				return state;
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
			public Integer run(Integer state) {
				assertThat(state).isEqualTo(42);
				return state;
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
		return ignore -> new Action<Integer>() {
			@Override
			public Integer run(Integer anInt) {
				return anInt * anInt;
			}

			@Override
			public String toString() {
				return "^2";
			}
		};
	}

	@SuppressWarnings("unchecked")
	private SequentialActionSequence<Integer> createSequence(Function<Integer, Action<Integer>>... actions) {
		return createSequence(actions.length, actions);
	}

	@SuppressWarnings("unchecked")
	private SequentialActionSequence<Integer> createSequence(int size, Function<Integer, Action<Integer>>... actions) {
		Iterator<Function<Integer, Action<Integer>>> iterator = Arrays.asList(actions).iterator();
		ActionGenerator<Integer> actionGenerator = new ActionGenerator<Integer>() {
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
		return new SequentialActionSequence<>(actionGenerator, size);
	}

}
