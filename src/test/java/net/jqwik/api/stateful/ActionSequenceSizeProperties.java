package net.jqwik.api.stateful;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class ActionSequenceSizeProperties {

	@Property
	boolean worksForActionSequences(@ForAll("actions") @Size(7) ActionSequence<Integer> sequence) {
		return sequence.run(0) == 7;
	}

	@Provide
	Arbitrary<ActionSequence<Integer>> actions() {
		return Arbitraries.sequences(Arbitraries.constant(model -> model + 1));
	}
}
