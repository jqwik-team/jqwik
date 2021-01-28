package net.jqwik.engine.properties.stateful;

import java.util.*;

import org.assertj.core.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;

class ActionSequenceProperties {

	@Property
	void createdSequencesDoTheirWork(@ForAll("xOrY") @Size(min = 2) ActionSequence<String> sequence) {
		String result = sequence.run("");

		Assertions.assertThat(sequence.runActions().size()).isGreaterThanOrEqualTo(2);
		Assertions.assertThat(result).hasSize(sequence.runActions().size());
		Assertions.assertThat(result.contains("x") || result.contains("y")).isTrue();
	}

	@Property
	void sequencesCanBeSized(@ForAll("ofSize5") ActionSequence<String> actions) {
		String result = actions.run("");
		Assertions.assertThat(actions.runActions()).hasSize(5);
	}

	@SuppressWarnings("WeakerAccess")
	@Provide
	ActionSequenceArbitrary<String> xOrY() {
		return Arbitraries.sequences(Arbitraries.oneOf(addX(), addY()));
	}

	@Provide
	ActionSequenceArbitrary<String> ofSize5() {
		return xOrY().ofSize(5);
	}

	@Property
	void preconditionsAreConsidered(@ForAll("xOrZ") ActionSequence<String> actions) {
		String result = actions.run("");

		Assertions.assertThat(result).hasSize(actions.runActions().size());
		Assertions.assertThat(result).contains("x");
		Assertions.assertThat(result).doesNotContain("z");
	}

	@Provide
	Arbitrary<ActionSequence<String>> xOrZ() {
		return Arbitraries.sequences(Arbitraries.oneOf(addX(), addZ()));
	}

	@Example
	void errorsAreWrappedInAssertionFailedError(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(error());
		Shrinkable<ActionSequence<String>> sequence = arbitrary.generator(10, true).next(random);

		Assertions.assertThatThrownBy(() -> sequence.value().run(""))
				  .isInstanceOf(AssertionFailedError.class).hasCauseInstanceOf(AssertionError.class);
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.just(model -> model + "x");
	}

	private Arbitrary<Action<String>> addY() {
		return Arbitraries.just(model -> model + "y");
	}

	private Arbitrary<Action<String>> addZ() {
		return Arbitraries.just(new Action<String>() {
			@Override
			public boolean precondition(String state) {
				return false;
			}

			@Override
			public String run(String state) {return state + "y";}
		});
	}

	private Arbitrary<Action<String>> error() {
		return Arbitraries.just(model -> {
			throw new AssertionError("test");
		});
	}

}
