package net.jqwik.engine.random;

import net.jqwik.api.random.*;

import org.apache.commons.rng.*;
import org.apache.commons.rng.simple.*;
import org.jetbrains.annotations.*;

import java.nio.*;

public class SeedBasedRandomState extends BaseRandomState {
	private final byte[] seed;
	private @Nullable RandomProviderState state;
	
	public SeedBasedRandomState(RandomSource algorithm, byte[] seed) {
		super(algorithm);
		this.seed = seed;
	}

	public byte[] getSeed() {
		return seed;
	}

	@Override
	public RestorableUniformRandomProvider createGenerator() {
		return getAlgorithm().create(seed);
	}

	@Override
	public RandomProviderState getState() {
		RandomProviderState state;
		synchronized (this) {
		    state = this.state;
			if (state == null) {
				state = createGenerator().saveState();
				this.state = state;
			}
		}
		return state;
	}
}
