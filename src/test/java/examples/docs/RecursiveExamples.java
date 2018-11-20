package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static net.jqwik.api.Arbitraries.*;

class RecursiveExamples {

	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
		return aSentence.endsWith(".");
	}

	@Provide
	Arbitrary<String> sentences() {
		Arbitrary<String> sentence =
			Combinators.combine(lazy(this::sentences), word())
					   .as((s, w) -> w + " " + s);

		return Arbitraries.frequencyOf(
			Tuple.of(1, word().map(w -> w + ".")),
			Tuple.of(3, sentence)
		);
	}

	private StringArbitrary word() {
		return Arbitraries.strings().alpha().ofLength(5);
	}


	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean sentencesEndWithAPoint_2(@ForAll("deterministic") String aSentence) {
		return aSentence.endsWith(".");
	}

	@Provide
	Arbitrary<String> deterministic() {
		Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
		Arbitrary<String> lastWord = word().map(w -> w + ".");

		return length.flatMap(l -> Arbitraries.recursive(() -> lastWord, this::prependWord, l));
	}

	private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
		return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
	}

}
