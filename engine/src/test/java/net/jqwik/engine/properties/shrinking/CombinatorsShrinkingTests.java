package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 100, shrinking = ShrinkingMode.FULL)
class CombinatorsShrinkingTests {

	@Property
	void shrinkCombineWithoutCondition(@ForAll JqwikRandom random) {
		Arbitrary<String> as =
			Combinators
				.combine(Arbitraries.integers(), Arbitraries.strings().alpha().ofMinLength(1))
				.as((i, s) -> i + s);

		String shrunkValue = falsifyThenShrink(as, random);

		Assertions.assertThat(shrunkValue).isIn("0A", "0a");
	}

	@Property
	void shrinkCombineWithCondition(@ForAll JqwikRandom random) {
		Arbitrary<String> as =
			Combinators
				.combine(Arbitraries.integers(), Arbitraries.strings().alpha().ofMinLength(1))
				.as((i, s) -> i + s);

		Falsifier<String> falsifier = aString -> aString.length() >= 3
														 ? TryExecutionResult.falsified(null)
														 : TryExecutionResult.satisfied();
		String shrunkValue = falsifyThenShrink(as, random, falsifier);

		Assertions.assertThat(shrunkValue).isIn("0AA", "10a", "10A", "-1a", "-1A");
	}

}
