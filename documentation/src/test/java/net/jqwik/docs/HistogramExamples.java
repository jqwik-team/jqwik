package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

class HistogramExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(format = Histogram.class)
	void integers(@ForAll("gaussians") int aNumber) {
		Statistics.collect(aNumber);
	}

	@Property(generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(format = Histogram.class)
	void groupedIntegers(@ForAll("gaussians") int aNumber) {
		// Statistics.collect(bucket);
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
