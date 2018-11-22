package net.jqwik.properties.stateful;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class ActionSequenceShrinkingTests {

	@Example
	void sequencesAreShrunkToSingleAction(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000).next(random);
		shrinkable.value().run(""); // to setup sequence

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(value -> {
			value.run("");
			return false;
		});

		while(sequence.next(() -> {}, ignore -> {}));
		ActionSequence<String> shrunkValue = sequence.current().value();
		String result = shrunkValue.run("");
		Assertions.assertThat(result).isEqualTo("x");
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.constant(model -> model + "x");
	}

	@Example
	void remainingActionsAreShrunkThemselves(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addStringOfLength2());
		Shrinkable<ActionSequence<String>> shrinkable = arbitrary.generator(1000).next(random);
		shrinkable.value().run(""); // to setup sequence

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(value -> {
			value.run("");
			return false;
		});

		while(sequence.next(() -> {}, ignore -> {}));
		ActionSequence<String> shrunkValue = sequence.current().value();
		String result = shrunkValue.run("");
		Assertions.assertThat(result).isEqualTo("AA");
	}

	private Arbitrary<Action<String>> addStringOfLength2() {
		return Arbitraries.strings().alpha().ofLength(2).map(s -> model -> model + s);
	}


}
