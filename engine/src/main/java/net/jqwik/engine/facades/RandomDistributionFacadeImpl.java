package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class RandomDistributionFacadeImpl extends RandomDistribution.RandomDistributionFacade {
	@Override
	public RandomDistribution biased() {
		return new BiasedRandomDistribution();
	}

	@Override
	public RandomDistribution uniform() {
		return new UniformRandomDistribution();
	}

	@Override
	public RandomDistribution gaussian(double borderSigma) {
		return new GaussianRandomDistribution(borderSigma);
	}
}
