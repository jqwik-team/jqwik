package net.jqwik.engine.execution.reporting;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
class SampleReportingTests {

	private final LineReporterStub lineReporter = new LineReporterStub();

	@Group
	class SampleReporterTests {
		@Example
		void sampleReporterReportsParametersIndividually() {
			List<Object> sample = Arrays.asList("this is a string", 42);
			List<String> parameterNames = Arrays.asList("aString", "aNumber");
			SampleReporter sampleReporter = new SampleReporter("Headline", sample, parameterNames);

			sampleReporter.reportTo(lineReporter);

			assertThat(lineReporter.lines).containsSequence(
				"Headline",
				"--------",
				"  aString: \"this is a string\"",
				"  aNumber: 42"
			);
		}

		@Example
		void headlineCanBeSkipped() {
			List<Object> sample = Arrays.asList("this is a string");
			List<String> parameterNames = Arrays.asList("aString");
			SampleReporter sampleReporter = new SampleReporter(null, sample, parameterNames);

			sampleReporter.reportTo(lineReporter);

			assertThat(lineReporter.lines).containsExactly(
				"",
				"  aString: \"this is a string\""
			);
		}

		@Example
		void whenLineLongerThan100charsParamIsReportedOnNextLineWithIndent() {
			String parameterValue = "This is a long string. This is a long string. This is a long string. This is a long string. " +
										"This is a long string. This is a long string. This is a long string. This is a long string.";
			List<Object> sample = Arrays.asList(parameterValue);
			List<String> parameterNames = Arrays.asList("aVeryLongString");
			SampleReporter sampleReporter = new SampleReporter("Headline", sample, parameterNames);

			sampleReporter.reportTo(lineReporter);

			assertThat(lineReporter.lines).containsSequence(
				"Headline",
				"--------",
				"  aVeryLongString:",
				String.format("    \"%s\"", parameterValue)
			);
		}
	}

	@Group
	class ValueReportTests {

		@Example
		void strings() {
			ValueReport report = ValueReport.of("this is a string", 42);

			Assertions.assertThat(report.compactLength()).isEqualTo(18);
			Assertions.assertThat(report.compactString()).isEqualTo("\"this is a string\"");
			report.report(lineReporter, 2);
			assertThat(lineReporter.lines).containsSequence(
				"    \"this is a string\""
			);
		}

		@Example
		void number() {
			ValueReport report = ValueReport.of(BigDecimal.valueOf(2.5), 42);

			Assertions.assertThat(report.compactLength()).isEqualTo(3);
			Assertions.assertThat(report.compactString()).isEqualTo("2.5");
			report.report(lineReporter, 2);
			assertThat(lineReporter.lines).containsSequence(
				"    2.5"
			);
		}

		@Example
		void objects() {
			Object object = new Object();
			ValueReport report = ValueReport.of(object, 42);

			Assertions.assertThat(report.compactLength()).isEqualTo(object.toString().length());
			Assertions.assertThat(report.compactString()).isEqualTo(object.toString());
			report.report(lineReporter, 2);
			assertThat(lineReporter.lines).containsSequence(
				"    " + object.toString()
			);
		}
	}
}
