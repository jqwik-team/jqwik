package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

class RecursiveExamples {

	@Property(tries = 10) @Report(Reporting.GENERATED)
	boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
		return aSentence.endsWith(".");
	}

	@Provide
	Arbitrary<String> sentences() {
		Arbitrary<String> sentence = Combinators.combine( //
			Arbitraries.lazy(this::sentences), //
			word() //
		).as((s, w) -> w + " " + s);
		return Arbitraries.oneOf( //
			word().map(w -> w + "."), //
			sentence, //
			sentence, //
			sentence //
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
		Arbitrary<Integer> length = Arbitraries.integers().between(1, 10);
		Arbitrary<String> lastWord = word().map(w -> w + ".");
		return length.flatMap(l -> deterministic(l, lastWord));
	}

	@Provide
	Arbitrary<String> deterministic(int length, Arbitrary<String> sentence) {
		if (length == 0) {
			return sentence;
		}
		Arbitrary<String> more = Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
		return deterministic(length - 1, more);
	}

}
