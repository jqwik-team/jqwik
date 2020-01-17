package net.jqwik.docs;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

/**
 * For feature https://github.com/jlink/jqwik/issues/75
 */
class StatisticsCoverageExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	void simpleStats(@ForAll RoundingMode mode) {
		Statistics.collect(mode);

//		Statistics.coverage(coverage -> {
//			coverage.check(RoundingMode.CEILING).percentage(p -> p > 5.0);
//			coverage.check(RoundingMode.FLOOR).count(c -> c > 2);
//		});
	}

	@Property(generation = GenerationMode.RANDOMIZED)
	void labeledStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
		String range = anInt < 3 ? "small" : "large";
		Statistics.label("range").collect(range);
		Statistics.label("value").collect(anInt);

//		Statistics.coverageOf("range", coverage -> coverage.check("small").percentage(p -> p > 20.0));
//		Statistics.coverageOf("value", coverage -> coverage.check(0).count(c -> c > 0));
	}

	@Property
	void integerStats(@ForAll int anInt) {
		Statistics.collect(anInt > 0 ? "positive" : "negative");

//		Statistics.coverage(coverage -> {
//			coverage.checkAll(Arrays.asList("positive", "negative")).percentage(p -> p > 5.0);
//		});
	}

	@Property
	void combinedIntegerStats(@ForAll int anInt) {
		String posOrNeg = anInt > 0 ? "positive" : "negative";
		String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";
		String bigOrSmall = Math.abs(anInt) > 50 ? "big" : "small";
		Statistics.collect(posOrNeg, evenOrOdd, bigOrSmall);

//		Statistics.coverage().checkAll(
//			Arrays.asList("positive", "negative"),
//			Arrays.asList("even", "odd"),
//			Arrays.asList("big", "small")
//		).percentage(p -> p > 5.0);
	}

}
