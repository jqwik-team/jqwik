package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.assertThat;

class FloatShrinkingTests {

	@Group
	class Doubles {
		@Example
		void doublesCurrentlyDontShrink() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			assertThat(shrinker.nextCandidates(2.0)).isEmpty();
		}


	}
}
