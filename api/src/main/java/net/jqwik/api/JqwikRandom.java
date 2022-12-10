package net.jqwik.api;

import net.jqwik.api.random.*;

import org.apache.commons.rng.*;

import java.util.*;

public interface JqwikRandom extends UniformRandomProvider {
	JqwikRandom jump();
	
	default JqwikRandom split() {
		return split(this);
	}
	
	JqwikRandom split(UniformRandomProvider source);
	
	JqwikRandomState saveState();
	
	void restoreState(JqwikRandomState state);
	
	default Random asJdkRandom() {
		return new Random() {
			@Override
			protected int next(int bits) {
				int next = JqwikRandom.this.nextInt();
				next &= ((1L << bits) - 1);
				return next;
			}

			@Override
			public long nextLong() {
				return JqwikRandom.this.nextLong();
			}
		};
	}
}
