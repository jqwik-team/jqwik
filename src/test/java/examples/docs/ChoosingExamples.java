package examples.docs;

import net.jqwik.api.*;

class ChoosingExamples {

	@Property
	void abcd(@ForAll("abcd") String aString) {
		Statistics.collect(aString);
	}

	@Provide
	Arbitrary<String> abcd() {
		return Arbitraries.of("a", "b", "c", "d");
	}

	@Property
	void abcdWithFrequencies(@ForAll("abcdWeighted") String aString) {
		Statistics.collect(aString);
	}

	@Provide
	Arbitrary<String> abcdWeighted() {
		return Arbitraries.frequency(
			Tuples.tuple(1, "a"),
			Tuples.tuple(5, "b"),
			Tuples.tuple(10, "c"),
			Tuples.tuple(20, "d")
		);
	}

	@Property
	@Label("should shrink to 'all'")
	boolean shrinkToLowestFalsifiedValue(@ForAll("someStrings") String aString) {
		return !aString.contains("a");
	}

	@Provide
	Arbitrary<String> someStrings() {
		return Arbitraries.frequency(
			Tuples.tuple(1, "oops"),
			Tuples.tuple(1, "all"),
			Tuples.tuple(2, "but"),
			Tuples.tuple(3, "character"),
			Tuples.tuple(4, "diploma"),
			Tuples.tuple(5, "emex"),
			Tuples.tuple(6, "fair"),
			Tuples.tuple(7, "ghost")
		);
	}
}
