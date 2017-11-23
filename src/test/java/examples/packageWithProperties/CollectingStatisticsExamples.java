package examples.packageWithProperties;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class CollectingStatisticsExamples {

	@Property
	void statisticsForPositiveIntegers(@ForAll @Positive int anInt) {
		String range = anInt < 10 ? "small" : (anInt < 100 ? "middle" : "large");
		Statistics.collect(range);
	}

}
