package net.jqwik.engine.random;

import net.jqwik.api.JqwikRandom;
import net.jqwik.api.random.JqwikRandomState;

import org.apache.commons.rng.*;
import org.apache.commons.rng.simple.*;

import java.util.*;

public class JqwikRandomImpl implements JqwikRandom {
	private final RandomSource algorithm;
	private final RestorableUniformRandomProvider delegate;

	public JqwikRandomImpl(RandomSource algorithm, RestorableUniformRandomProvider delegate) {
		this.algorithm = algorithm;
		this.delegate = delegate;
	}

	@Override
	public long nextLong() {
		return delegate.nextLong();
	}
	
	@Override
	public JqwikRandom split(UniformRandomProvider source) {
		if (delegate instanceof SplittableUniformRandomProvider) {
			RestorableUniformRandomProvider next = (RestorableUniformRandomProvider) ((SplittableUniformRandomProvider) delegate).split();
			return new JqwikRandomImpl(algorithm, next);
		}
		throw new IllegalStateException("Base random source should be splittable, actual is " + delegate.getClass().getSimpleName());
	}

	@Override
	public JqwikRandom jump() {
		if (delegate instanceof JumpableUniformRandomProvider) {
			RestorableUniformRandomProvider next = (RestorableUniformRandomProvider) ((JumpableUniformRandomProvider) delegate).jump();
			return new JqwikRandomImpl(algorithm, next);
		}
		throw new IllegalStateException("Base random source should be jumpable, actual is " + delegate.getClass().getSimpleName());
	}

	@Override
	public JqwikRandomState saveState() {
		return new InMemoryRandomState(algorithm, delegate.saveState());
	}

	@Override
	public void restoreState(JqwikRandomState state) {
		if (state.getAlgorithm() != algorithm) {
			throw new IllegalArgumentException(
				"Cannot restore state from different algorithm. Current: " + algorithm + ", new: " + state.getAlgorithm()
			);
		}
		if (state instanceof BaseRandomState) {
			delegate.restoreState(((BaseRandomState) state).getState());
		}
	}
}
