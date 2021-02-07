package net.jqwik.engine.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.arbitraries.*;

public class DefaultActionSequenceArbitrary<M> extends TypedCloneable implements ActionSequenceArbitrary<M> {

	private final Arbitrary<Action<M>> actionArbitrary;

	private int minSize = 1;
	private int maxSize = 0;

	public DefaultActionSequenceArbitrary(Arbitrary<? extends Action<M>> actionArbitrary) {
		//noinspection unchecked
		this.actionArbitrary = (Arbitrary<Action<M>>) actionArbitrary;
	}

	@Override
	public ActionSequenceArbitrary<M> ofMinSize(int minSize) {
		DefaultActionSequenceArbitrary<M> clone = typedClone();
		clone.minSize = Math.max(1, minSize);
		return clone;
	}

	@Override
	public ActionSequenceArbitrary<M> ofMaxSize(int maxSize) {
		DefaultActionSequenceArbitrary<M> clone = typedClone();
		clone.maxSize = Math.max(Math.max(1, maxSize), minSize);
		return clone;
	}

	@Override
	public RandomGenerator<ActionSequence<M>> generator(int genSize) {
		final int effectiveMaxSize =
			maxSize != 0 ? maxSize
				: (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return new ActionSequenceGenerator<>(actionArbitrary, genSize, minSize, effectiveMaxSize);
	}

	@Override
	public EdgeCases<ActionSequence<M>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

}
