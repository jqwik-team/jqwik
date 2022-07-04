package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class RandomDistributionFacadeImpl extends RandomDistribution.RandomDistributionFacade {

	private static final BiasedRandomDistribution BIASED_RANDOM_DISTRIBUTION = new BiasedRandomDistribution();
	private static final UniformRandomDistribution UNIFORM_RANDOM_DISTRIBUTION = new UniformRandomDistribution();

	@Override
	public RandomDistribution biased() {
		return BIASED_RANDOM_DISTRIBUTION;
	}

	@Override
	public RandomDistribution uniform() {
		return UNIFORM_RANDOM_DISTRIBUTION;
	}

	@Override
	public RandomDistribution gaussian(double borderSigma) {
		return new GaussianRandomDistribution(borderSigma);
	}
}
