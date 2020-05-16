package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

class HistogramExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(format = Histogram.class)
	void integers(@ForAll("gaussians") int aNumber) {
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

	@Property(generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void integersInRanges(@ForAll @IntRange(min = -1000, max = 1000) int aNumber) {
		Statistics.collect(aNumber);
	}

}
