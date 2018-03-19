package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.*;
import org.assertj.core.api.*;

import java.util.*;

class ActionSequenceShrinkingTests {

	@Example
	void sequencesAreShrunkToSingleAction(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addX());

		ActionSequence<String> shrunkValue = ArbitraryTestHelper.shrinkToEnd(arbitrary, random);
		String result = shrunkValue.run("");
		Assertions.assertThat(result).isEqualTo("x");
	}

	private Arbitrary<Action<String>> addX() {
		return Arbitraries.constant(model -> model + "x");
	}

	@Example
	void remainingActionsAreShrunkThemselves(@ForAll Random random) {
		Arbitrary<ActionSequence<String>> arbitrary = Arbitraries.sequences(addStringOfLength2());

		ActionSequence<String> shrunkValue = ArbitraryTestHelper.shrinkToEnd(arbitrary, random);
		String result = shrunkValue.run("");
		Assertions.assertThat(result).isEqualTo("AA");
	}

	private Arbitrary<Action<String>> addStringOfLength2() {
		return Arbitraries.strings().alpha().ofLength(2).map(s -> (Action<String>) model -> model + s);
	}


}
