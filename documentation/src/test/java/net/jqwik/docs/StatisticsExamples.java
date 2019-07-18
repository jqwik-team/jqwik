package net.jqwik.docs;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class StatisticsExamples {

	@Property(generation = GenerationMode.RANDOMIZED)
	void simpleStats(@ForAll RoundingMode mode) {
		Statistics.collect(mode);
	}

	@Property(generation = GenerationMode.RANDOMIZED)
	void labeledStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
		String range = anInt < 3 ? "small" : "large";
		Statistics.label("range").collect(range);
		Statistics.label("value").collect(anInt);
	}

	@Property
	void integerStats(@ForAll int anInt) {
		Statistics.collect(anInt > 0 ? "positive" : "negative");
	}

	@Property
	void combinedIntegerStats(@ForAll int anInt) {
		String posOrNeg = anInt > 0 ? "positive" : "negative";
		String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";
		String bigOrSmall = Math.abs(anInt) > 50 ? "big" : "small";
		Statistics.collect(posOrNeg, evenOrOdd, bigOrSmall);
	}

	@Property
	void twoParameterStats(@ForAll @Size(min = 1, max = 10) List<Integer> aList, //
			@ForAll @IntRange(min = 0, max = 10) int index //
	) {
		Statistics.collect(aList.size() > index ? "index within size" : null);
	}

	@Property(tries = 100000)
	void statsWithRoundedNumbers(@ForAll @IntRange(max = 100000) int anInt) {
		String classifier = anInt < 10 ? "smallest" //
				: anInt < 1000 ? "small" //
						: anInt < 80000 ? "normal" //
								: "big";
		Statistics.collect(classifier);
	}

	@Property(tries = 100000)
	void statsWithDecimalNumbers(@ForAll int anInt) {
		String classifier = anInt == 3 ? "three" //
				: anInt < 1000 ? "small" //
						: anInt < 80000 ? "normal" //
								: "big";
		Statistics.collect(classifier);
	}

}
