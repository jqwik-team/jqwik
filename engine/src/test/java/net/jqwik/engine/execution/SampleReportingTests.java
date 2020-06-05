package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class SampleReportingTests {

	@Example
	void sampleReporterReportsParametersIndividually() {
		List<Object> sample = Arrays.asList("this is a string", 42);
		List<String> parameterNames = Arrays.asList("aString", "aNumber");
		SampleReporter sampleReporter = new SampleReporter("Headline", sample, parameterNames);

		StringBuilder reportBuilder = new StringBuilder();
		sampleReporter.reportTo(reportBuilder);

		assertThat(reportBuilder).containsSequence(
			String.format("Headline%n"),
			String.format("--------%n"),
			String.format("  aString: \"this is a string\"%n"),
			String.format("  aNumber: 42%n")
		);
	}
}
