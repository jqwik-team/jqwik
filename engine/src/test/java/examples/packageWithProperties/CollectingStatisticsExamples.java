package examples.packageWithProperties;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;

public class CollectingStatisticsExamples {

	@Property
	void statisticsForPositiveIntegers(@ForAll @Positive int anInt) {
		String range = anInt < 10 ? "small" : (anInt < 100 ? "middle" : "large");
		Statistics.collect(range);
	}

	@Property(tries = 10000)
	void statisticsForTwoParams(
		@ForAll @StringLength(max = 10) String aString,
		@ForAll @IntRange(max = 10) int aLength
	) {
		String range = aString.length() == 0 ? "empty" : (aString.length() < 8 ? "normal" : "big");
		String length = aString.length() == aLength ? "exact" : (aString.length() > aLength ? "smaller" : "larger");
		Statistics.collect(range, length);
	}
}
