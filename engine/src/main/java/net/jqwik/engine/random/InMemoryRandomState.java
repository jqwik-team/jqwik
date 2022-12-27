package net.jqwik.engine.random;

import org.apache.commons.rng.*;
import org.apache.commons.rng.simple.*;

public class InMemoryRandomState extends BaseRandomState {
	private final RandomProviderState state;

	protected InMemoryRandomState(RandomSource algorithm, RandomProviderState state) {
		super(algorithm);
		this.state = state;
	}

	@Override
	public RandomProviderState getState() {
		return state;
	}

	@Override
	public RestorableUniformRandomProvider createGenerator() {
		RestorableUniformRandomProvider provider = getAlgorithm().create();
		provider.restoreState(state);
		return provider;
	}
}
