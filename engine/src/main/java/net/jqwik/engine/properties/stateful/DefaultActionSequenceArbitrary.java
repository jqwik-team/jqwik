package net.jqwik.engine.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.arbitraries.*;

public class DefaultActionSequenceArbitrary<M> extends TypedCloneable implements ActionSequenceArbitrary<M> {

	private final Arbitrary<Action<M>> actionArbitrary;

	private int size = 0;

	public DefaultActionSequenceArbitrary(Arbitrary<? extends Action<M>> actionArbitrary) {
		//noinspection unchecked
		this.actionArbitrary = (Arbitrary<Action<M>>) actionArbitrary;
	}

	@Override
	public ActionSequenceArbitrary<M> ofMinSize(int minSize) {
		return this;
	}

	@Override
	public ActionSequenceArbitrary<M> ofSize(int size) {
		DefaultActionSequenceArbitrary<M> clone = typedClone();
		clone.size = size;
		return clone;
	}

	@Override
	public RandomGenerator<ActionSequence<M>> generator(int genSize) {
		final int effectiveMaxSize =
			size != 0 ? size : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return new ActionSequenceGenerator<>(actionArbitrary, genSize, effectiveMaxSize);
	}

	@Override
	public EdgeCases<ActionSequence<M>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

}
