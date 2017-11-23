package examples.bugs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class PositiveIntegersGenerationBug {

	@Property
	void statisticsForSmallIntegers(@ForAll @IntRange(min = 1, max = 5) int anInt) {
		Statistics.collect(anInt);
	}

//	@Property
//	void statisticsForPositiveIntegers(@ForAll @Positive int anInt) {
//		String range = anInt < 10 ? "small" : (anInt < 100 ? "middle" : "large");
//		Statistics.collect(range);
//	}

	@Property
	void statisticsForPositiveIntegers(@ForAll @IntRange(min = 0, max = 1000) int anInt) {
		String range = anInt < 100 ? "small" : (anInt < 500 ? "middle" : "large");
		Statistics.collect(range);
	}

}
