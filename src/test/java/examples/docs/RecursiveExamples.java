package examples.docs;

import net.jqwik.api.*;

class RecursiveExamples {

	@Property(reporting = Reporting.GENERATED)
	boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
		return aSentence.endsWith(".");
	}

	@Provide
	Arbitrary<String> sentences() {
			Arbitrary<String> word = Arbitraries.strings().alpha().ofLength(5);
			Arbitrary<String> sentence = Combinators.combine(
				Arbitraries.recursive(this::sentences) , word)
				.as((s, w) -> w + " " + s);
			return Arbitraries.oneOf(
				word.map(w -> w + "."),
				sentence,
				sentence,
				sentence
			);
	}
}
