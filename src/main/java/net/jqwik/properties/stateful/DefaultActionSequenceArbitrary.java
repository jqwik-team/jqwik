package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;

public class DefaultActionSequenceArbitrary<M> extends AbstractArbitraryBase implements ActionSequenceArbitrary<M> {

	private final Arbitrary<Action<M>> actionArbitrary;
	private int size = 0;

	public DefaultActionSequenceArbitrary(Arbitrary<Action<M>> actionArbitrary) {
		this.actionArbitrary = actionArbitrary;
	}

	@Override
	public DefaultActionSequenceArbitrary<M> ofSize(int size) {
		DefaultActionSequenceArbitrary<M> clone = typedClone();
		clone.size = size;
		return clone;
	}

	@Override
	public RandomGenerator<ActionSequence<M>> generator(int genSize) {
		final int numberOfActions =
			size != 0 ? size
				: (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return new ActionSequenceGenerator<>(actionArbitrary, genSize, numberOfActions);
	}

}
