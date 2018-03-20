package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import org.assertj.core.api.*;
import org.opentest4j.*;

import java.util.*;

class ActionSequenceProperties {

	@Property
	void createdSequencesDoTheirWork(@ForAll("xOrY") ActionSequence<String> actions) {
		String result = actions.run("");

		Assertions.assertThat(result).hasSize(actions.sequence().size());
		Assertions.assertThat(result).contains("x");
		Assertions.assertThat(result).contains("y");
	}

	@Provide
	Arbitrary<ActionSequence<String>> xOrY() {
		return Arbitraries.sequences(Arbitraries.oneOf(addX(), addY()));
	}

	@Property
	void preconditionsAreConsidered(@ForAll("xOrZ") ActionSequence<String> actions) {
		String result = actions.run("");

		Assertions.assertThat(result).hasSize(actions.sequence().size());
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
		Shrinkable<ActionSequence<String>> sequence = arbitrary.generator(10).next(random);

		Assertions.assertThatThrownBy(() -> sequence.value().run(""))
				  .isInstanceOf(AssertionFailedError.class).hasCauseInstanceOf(AssertionError.class);
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.constant(model -> model + "x");
	}

	private Arbitrary<Action<String>> addY() {
		return Arbitraries.constant(model -> model + "y");
	}

	private Arbitrary<Action<String>> addZ() {
		return Arbitraries.constant(new Action<String>() {
			@Override
			public boolean precondition(String model) {
				return false;
			}

			@Override
			public String run(String model) {return model + "y";}
		});
	}

	private Arbitrary<Action<String>> error() {
		return Arbitraries.constant(model -> {
			throw new AssertionError("test");
		});
	}

}
