package net.jqwik.testing;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static net.jqwik.testing.ShrinkingSupport.*;

class TestingSupportTests {

	@Example
	void assertAllGenerated(@ForAll JqwikRandom random) {
		Arbitrary<String> strings = Arbitraries.just("hello");
		TestingSupport.checkAllGenerated(strings.generator(1000, true), random, (Predicate<String>) s -> s.equals("hello"));
	}

	@Example
	void shrinkToMinimal(@ForAll JqwikRandom random) {
		Arbitrary<String> strings = Arbitraries.strings().alpha().ofMaxLength(10);

		String shrunkValue = falsifyThenShrink(strings, random);
		Assertions.assertThat(shrunkValue).isEqualTo("");
	}

	@Example
	void collectEdgeCases() {
		Arbitrary<String> strings = Arbitraries.strings().alpha().ofMaxLength(10);

		Set<String> edgeCases = TestingSupport.collectEdgeCaseValues(strings.edgeCases());
		Assertions.assertThat(edgeCases).containsExactlyInAnyOrder("", "A", "a", "Z", "z");
	}
}
