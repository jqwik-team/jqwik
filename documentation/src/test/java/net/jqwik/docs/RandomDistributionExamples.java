package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

class RandomDistributionExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(format = Histogram.class)
	void gaussianDistributedIntegers(@ForAll("gaussians") int aNumber) {
		Statistics.collect(aNumber);
	}

	@Provide
	Arbitrary<Integer> gaussians() {
		return Arbitraries
				   .integers()
				   .between(0, 20)
				   .shrinkTowards(10)
				   .withDistribution(RandomDistribution.gaussian());
	}
}
