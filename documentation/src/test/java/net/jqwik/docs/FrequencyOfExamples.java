package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.statistics.Statistics;

class FrequencyOfExamples {

	@Property(tries = 100)
	@Report(Reporting.GENERATED)
	boolean intsAreCreatedFromOneOfThreeArbitraries(@ForAll("oneOfThree") int anInt) {
		String classifier = anInt < -1000 ? "below" : anInt > 1000 ? "above" : "one";
		Statistics.collect(classifier);

		return anInt < -1000 //
				   || Math.abs(anInt) == 1 //
				   || anInt > 1000;
	}

	@Provide
	Arbitrary<Integer> oneOfThree() {
		IntegerArbitrary below1000 = Arbitraries.integers().between(-1050, -1001);
		IntegerArbitrary above1000 = Arbitraries.integers().between(1001, 1050);
		Arbitrary<Integer> oneOrMinusOne = Arbitraries.samples(-1, 1);

		return Arbitraries.frequencyOf(
			Tuple.of(1, below1000),
			Tuple.of(3, above1000),
			Tuple.of(6, oneOrMinusOne)
		);
	}
}
