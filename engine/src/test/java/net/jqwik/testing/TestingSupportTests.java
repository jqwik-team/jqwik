package net.jqwik.testing;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class TestingSupportTests {

	@Example
	void assertAllGenerated(@ForAll Random random) {
		Arbitrary<String> strings = Arbitraries.just("hello");

		TestingSupport.assertAllGenerated(strings.generator(1000), random, s -> s.equals("hello"));
	}

	@Example
	void shrinkToMinimal(@ForAll Random random) {
		Arbitrary<String> strings = Arbitraries.strings().alpha().ofMaxLength(10);

		String shrunkValue = ShrinkingSupport.falsifyThenShrink(strings, random);
		Assertions.assertThat(shrunkValue).isEqualTo("");
	}

	@Example
	void collectEdgeCases() {
		Arbitrary<String> strings = Arbitraries.strings().alpha().ofMaxLength(10);

		Set<String> edgeCases = TestingSupport.collectEdgeCases(strings.edgeCases());
		Assertions.assertThat(edgeCases).containsExactlyInAnyOrder("", "A", "a", "Z", "z");
	}
}
