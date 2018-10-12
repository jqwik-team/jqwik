package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static net.jqwik.api.GenerationMode.*;

class ExhaustiveGenerationExamples {

	@Property(generation = EXHAUSTIVE)
	@Report(Reporting.GENERATED)
	void allNumbersFrom1to10(@ForAll @IntRange(min = 1, max = 10) int anInt) {
	}
}
