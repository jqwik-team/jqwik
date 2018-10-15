package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static net.jqwik.api.GenerationMode.*;

class ExhaustiveGenerationExamples {

	@Property(generation = EXHAUSTIVE)
	@Report(Reporting.GENERATED)
	void all_numbers_from_1_to_10(@ForAll @IntRange(min = 1, max = 10) int anInt) {
	}

	@Provide
	Arbitrary<String> samples() {
		return Arbitraries.of("A", "B", "C");
	}

	@Property(generation = EXHAUSTIVE)
	@Report(Reporting.GENERATED)
	void all_pairs_of_boolean_and_samples(@ForAll boolean aBool, @ForAll("samples") String sample) {
	}

	@Property(generation = EXHAUSTIVE)
	@Report(Reporting.GENERATED)
	void quadruplet_with_10000_tries(
		@ForAll @IntRange(min = 1, max = 10) int int1,
		@ForAll @IntRange(min = 11, max = 20) int int2,
		@ForAll @IntRange(min = 21, max = 30) int int3,
		@ForAll @IntRange(min = 31, max = 40) int int4
	) {
	}

}
