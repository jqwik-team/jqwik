package net.jqwik.docs.statistics;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;

/**
 * For feature https://github.com/jlink/jqwik/issues/75
 */
class StatisticsCoverageExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	void simpleStats(@ForAll RoundingMode mode) {
		Statistics.collect(mode);

		Statistics.coverage(coverage -> {
			coverage.check(RoundingMode.CEILING).percentage(p -> p > 5.0);
			coverage.check(RoundingMode.FLOOR).count(c -> c > 2);
		});
	}

	@Property(generation = GenerationMode.RANDOMIZED)
	void labeledStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
		String range = anInt < 3 ? "small" : "large";
		Statistics.label("range").collect(range);
		Statistics.label("value").collect(anInt);

		Statistics.coverageOf("range", coverage -> coverage.check("small").percentage(p -> p > 20.0));
		Statistics.coverageOf("value", coverage -> coverage.check(0).count(c -> c > 0));
	}

	@Property
	void combinedStats(@ForAll int anInt) {
		String posOrNeg = anInt > 0 ? "positive" : "negative";
		String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";

		Statistics.collect(posOrNeg, evenOrOdd);

		Statistics.coverage(coverage -> {
			coverage.check("positive", "even").count(c -> c > 0);
			coverage.check("negative", "even").count(c -> c > 0);
			coverage.check("positive", "odd").count(c -> c > 0);
			coverage.check("negative", "odd").count(c -> c > 0);
		});
	}

	@Property
	void combinedStats2(@ForAll int anInt) {
		String posOrNeg = anInt > 0 ? "positive" : "negative";
		String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";

		Statistics.collect(posOrNeg, evenOrOdd);

//		Statistics.coverage(coverage -> {
//			coverage.checkAllCombinations(
//				asList("positive", "negative"),
//				asList("even", "odd")
//			).count(c -> c > 0);
//		});
	}

	// Different kind of statistics API:

	enum Sign {POSITIVE, NEGATIVE}

	enum Oddity {EVEN, ODD}

	@Property
	void combinedStats3(@ForAll int anInt) {
		Statistics.collect(anInt);

//		Statistics.coverage(coverage -> {
//			Function<Integer, Sign> posOrNeg = i -> i > 0 ? Sign.POSITIVE : Sign.NEGATIVE;
//			Function<Integer, Oddity>  evenOrOdd = i -> i % 2 == 0 ? Oddity.EVEN : Oddity.ODD;
//			Classifier classifier = Classifier.from(posOrNeg, evenOrOdd);
//
//			coverage.classify(posOrNeg, evenOrOdd)
//					.checkAll().count(c -> c > 0);
//					.check(Sign.NEGATIVE, Oddity.ODD).percentage(p -> p > 5.0);
//		});
	}

}
