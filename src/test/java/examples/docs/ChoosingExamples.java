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
			Tuple.of(1, "a"),
			Tuple.of(5, "b"),
			Tuple.of(10, "c"),
			Tuple.of(20, "d")
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
			Tuple.of(1, "oops"),
			Tuple.of(1, "all"),
			Tuple.of(2, "but"),
			Tuple.of(3, "character"),
			Tuple.of(4, "diploma"),
			Tuple.of(5, "emex"),
			Tuple.of(6, "fair"),
			Tuple.of(7, "ghost")
		);
	}
}
