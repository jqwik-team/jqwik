package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.stateful.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@SuppressWarnings("unchecked")
class ActionSequenceShrinkingTests {

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	@ExpectFailure(checkResult = FailedAndShrunkToAddX.class, failureType = AssertionError.class)
	void dontShrinkToActionsWithOnlyFailingPreconditions(@ForAll("addXorY") @Size(3) ActionSequence<String> sequence) {
		String model = sequence.run("");
		assertThat(model).isEmpty();
	}

	private class FailedAndShrunkToAddX implements Consumer<PropertyExecutionResult> {
		@SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
		@Override
		public void accept(PropertyExecutionResult result) {
			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(AssertionError.class);

			List<Object> sample = result.falsifiedParameters().get();
			assertThat(sample).hasSize(1);

			ActionSequence<String> sequence = (ActionSequence<String>) sample.get(0);
			assertThat(sequence.runActions()).hasSize(1);
			assertThat(sequence.runActions().get(0).toString()).isEqualTo("addX");
		}
	}

	@Provide
	Arbitrary<ActionSequence<String>> addXorY() {
		return Arbitraries.sequences(
			Arbitraries.oneOf(addX(), addYIfNotEmpty())
		);
	}

	@Example
	void sequencesAreShrunkToSingleAction(@ForAll JqwikRandom random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000, true).next(random);
		shrinkable.value().run(""); // to setup sequence

		TestingFalsifier<ActionSequence<String>> falsifier = (ActionSequence<String> seq) -> {
			seq.run("");
			return false;
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);

		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.run("")).isEqualTo("x");
	}

	@Example
	void dontShrinkBelow1Action(@ForAll JqwikRandom random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000, true).next(random);
		shrinkable.value().run(""); // to setup sequence

		TestingFalsifier<ActionSequence<String>> falsifier = (ActionSequence<String> seq) -> {
			seq.run("");
			throw failAndCatch(null);
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, failAndCatch(null));

		assertThat(shrunkValue.runActions()).hasSize(1);
	}

	@Example
	void remainingActionsAreShrunkThemselves(@ForAll JqwikRandom random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addStringOfLength2());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000, true).next(random);
		shrinkable.value().run(""); // to setup sequence

		TestingFalsifier<ActionSequence<String>> falsifier = (ActionSequence<String> seq) -> {
			seq.run("");
			return false;
		};

		ActionSequence<String> shrunkValue = shrink(shrinkable, falsifier, null);

		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isIn("aa", "AA");
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.just(new Action<String>() {
			@Override
			public String run(final String model) {
				return model + "x";
			}

			@Override
			public String toString() {
				return "addX";
			}
		});
	}

	private Arbitrary<Action<String>> addYIfNotEmpty() {
		return Arbitraries.just(new Action<String>() {
			@Override
			public boolean precondition(final String state) {
				return !state.isEmpty();
			}

			@Override
			public String run(final String model) {
				return model + "y";
			}

			@Override
			public String toString() {
				return "addYIfNotEmpty";
			}
		});
	}

	private Arbitrary<Action<String>> addStringOfLength2() {
		return Arbitraries.strings().alpha().ofLength(2).map(s -> model -> model + s);
	}

	private AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}


}
