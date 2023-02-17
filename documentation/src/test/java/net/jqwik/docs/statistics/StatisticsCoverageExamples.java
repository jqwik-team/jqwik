package net.jqwik.docs.statistics;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

/**
 * For feature https://github.com/jqwik-team/jqwik/issues/75
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

		Statistics.label("range")
				  .collect(range)
				  .coverage(coverage -> coverage.check("small").percentage(p -> p > 20.0));

		Statistics.label("value")
				  .collect(anInt)
				  .coverage(coverage -> coverage.check(0).count(c -> c > 0));
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
	@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
	void queryStatistics(@ForAll int anInt) {
		Statistics.collect(anInt);

		Statistics.coverage(coverage -> {
			Predicate<List<Integer>> isZero = params -> params.get(0) == 0;
			coverage.checkQuery(isZero).percentage(p -> p > 5.0);
		});
	}

	@Property
	@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
	void patternStatistics(@ForAll @NumericChars String aString) {
		Statistics.collect(aString);

		Statistics.coverage(coverage -> {
			coverage.checkPattern("0.*").percentage(p -> p >= 10.0);
		});
	}
}
