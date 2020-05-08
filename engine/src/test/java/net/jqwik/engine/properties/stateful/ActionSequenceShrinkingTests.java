package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

class ActionSequenceShrinkingTests {

	@Example
	void sequencesAreShrunkToSingleAction(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000).next(random);
		shrinkable.value().run(""); // to setup sequence

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink((TestingFalsifier<ActionSequence<String>>) value -> {
			value.run("");
			return false;
		});

		while (sequence.next(() -> {}, ignore -> {})) ;
		ActionSequence<String> shrunkValue = sequence.current().value();
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isEqualTo("x");
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	@ExpectFailure(checkResult = FailedAndShrunkToAddX.class)
	void dontShrinkToActionsWithOnlyFailingPreconditions(@ForAll("addXorY") @Size(max = 3) ActionSequence<String> sequence) {
		String model = sequence.run("");
		assertThat(model).isEmpty();
	}

	private class FailedAndShrunkToAddX implements Consumer<PropertyExecutionResult> {
		@SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
		@Override
		public void accept(PropertyExecutionResult result) {
			assertThat(result.falsifiedSample()).isPresent();
			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(AssertionError.class);

			List<Object> sample = result.falsifiedSample().get();
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
	void dontShrinkUnderMinSize(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX()).ofMinSize(3);
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000).next(random);
		shrinkable.value().run(""); // to setup sequence

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink((TestingFalsifier<ActionSequence<String>>) value -> {
			value.run("");
			throw new AssertionError();
		});

		while (sequence.next(() -> {}, ignore -> {})) ;
		ActionSequence<String> shrunkValue = sequence.current().value();
		assertThat(shrunkValue.runActions()).hasSize(3);
	}

	@Example
	void remainingActionsAreShrunkThemselves(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addStringOfLength2());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000).next(random);
		shrinkable.value().run(""); // to setup sequence

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink((TestingFalsifier<ActionSequence<String>>) value -> {
			value.run("");
			return false;
		});

		while (sequence.next(() -> {}, ignore -> {})) ;
		ActionSequence<String> shrunkValue = sequence.current().value();
		assertThat(shrunkValue.runActions()).hasSize(1);
		assertThat(shrunkValue.runActions().get(0).run("")).isIn("aa", "AA");
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.constant(new Action<String>() {
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
		return Arbitraries.constant(new Action<String>() {
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

}
