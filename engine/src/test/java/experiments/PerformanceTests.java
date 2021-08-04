package experiments;

import java.util.*;

import net.jqwik.api.*;

class PerformanceTests {

	@Example
	void testing() {

		Arbitrary<String> arbitrary = combinedStrings();

		RandomGenerator<String> generator = arbitrary.generator(1000, true);
		Random random = new Random(42L);

		long before = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			generator.next(random);
			// System.out.println(generator.next(random).value());
		}
		long after = System.currentTimeMillis();

		System.out.println(after - before);

	}

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
}

