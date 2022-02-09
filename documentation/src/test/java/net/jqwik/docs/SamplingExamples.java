package net.jqwik.docs;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class SamplingExamples {

	@Property(tries = 10)
	void generateSingleSample() {
		Arbitrary<String> strings = Arbitraries.of("string1", "string2", "string3", null);
		String aString = strings.sample();
		assertThat(aString).isIn("string1", "string2", "string3", null);
	}

	@Example
	void generateStreamOfSamples() {
		List<String> values = Arrays.asList("string1", "string2", "string3");
		Arbitrary<String> strings = Arbitraries.of(values);
		Stream<? extends String> streamOfStrings = strings.sampleStream().limit(100);

		assertThat(streamOfStrings).allMatch(values::contains);
	}
}
