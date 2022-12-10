package net.jqwik.engine.random;

import net.jqwik.api.random.*;

import org.apache.commons.rng.*;
import org.apache.commons.rng.simple.*;

public abstract class BaseRandomState implements JqwikRandomState {
	private final RandomSource algorithm;

	protected BaseRandomState(RandomSource algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public RandomSource getAlgorithm() {
		return algorithm;
	}

	abstract public RandomProviderState getState();
	
	abstract public RestorableUniformRandomProvider createGenerator();
}
