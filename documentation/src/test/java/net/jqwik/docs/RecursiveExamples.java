package net.jqwik.docs;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static net.jqwik.api.Arbitraries.*;

@Group
@PropertyDefaults(afterFailure = AfterFailureMode.RANDOM_SEED)
class RecursiveExamples {

	@Group
	class LazyOf {
		@Property
		@Report(Reporting.GENERATED)
		boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
			return !aSentence.contains("x");
			// return aSentence.endsWith(".");
		}

		@Provide
		Arbitrary<String> sentences() {
			return Arbitraries.lazyOf(
				() -> word().map(w -> w + "."),
				this::sentence,
				this::sentence,
				this::sentence
			);
		}

		private Arbitrary<String> sentence() {
			return Combinators.combine(sentences(), word())
							  .as((s, w) -> w + " " + s);
		}
	}

	@Group
	class Lazy {
		@Property
		@Report(Reporting.GENERATED)
		boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
			return !aSentence.contains("x");
			// return aSentence.endsWith(".");
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
	}

	@Group
	class Recursive {

		@Property
		@Report(Reporting.GENERATED)
		boolean sentencesEndWithAPoint(@ForAll("deterministic") String aSentence) {
			// return aSentence.endsWith(".");
			return !aSentence.contains("x");
		}

		@Provide
		Arbitrary<String> deterministic() {
			Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
			Arbitrary<String> lastWord = word().map(w -> w + ".");

			return length.flatMap(depth -> Arbitraries.recursive(
				() -> lastWord,
				this::prependWord,
				depth
			));
		}

		private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
			return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
		}
	}

	private StringArbitrary word() {
		return Arbitraries.strings().alpha().ofLength(5);
	}

}
