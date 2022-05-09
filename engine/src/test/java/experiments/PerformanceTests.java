package experiments;

import java.util.*;

import net.jqwik.api.*;

class PerformanceTests {

	@Property(tries = 10000)
	void testingForAll(@ForAll("combinedStrings") String aString) {

	}

	@Provide
	private Arbitrary<String> combinedStrings() {
		Arbitrary<String> arbitrary =
				Builders.withBuilder(() -> "")
						   .use(Arbitraries.strings().alpha().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().numeric().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().withChars("+-*/%$?§!").ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().alpha().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().numeric().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().withChars("+-*/%$?§!").ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().alpha().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().numeric().ofMinLength(1)).in((a, b) -> a + b)
						   .use(Arbitraries.strings().withChars("+-*/%$?§!").ofMinLength(1)).in((a, b) -> a + b)
						   .build();//.withoutEdgeCases();
		return arbitrary;
	}

	@Property(tries = 10000)
	void testingForAll2(@ForAll("combinedStrings2") String aString) {

	}
}

