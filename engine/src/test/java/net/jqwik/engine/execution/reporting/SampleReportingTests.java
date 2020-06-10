package net.jqwik.engine.execution.reporting;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
class SampleReportingTests {

	private final LineReporterStub lineReporter = new LineReporterStub();

	@Group
	class SampleReporterTests {
		@Example
		void sampleReporterReportsParametersIndividually() {
			List<Object> sample = asList("this is a string", 42);
			List<String> parameterNames = asList("aString", "aNumber");
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
			List<Object> sample = asList("this is a string");
			List<String> parameterNames = asList("aString");
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
			List<Object> sample = asList(parameterValue);
			List<String> parameterNames = asList("aVeryLongString");
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
			ValueReport report = ValueReport.of("this is a string");

			Assertions.assertThat(report.compactLength()).isEqualTo(18);
			Assertions.assertThat(report.compactString()).isEqualTo("\"this is a string\"");
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    \"this is a string\""
			);
		}

		@Example
		void number() {
			ValueReport report = ValueReport.of(BigDecimal.valueOf(2.5));

			Assertions.assertThat(report.compactLength()).isEqualTo(3);
			Assertions.assertThat(report.compactString()).isEqualTo("2.5");
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    2.5"
			);
		}

		@Example
		void objects() {
			Object object = new Object();
			ValueReport report = ValueReport.of(object);

			Assertions.assertThat(report.compactLength()).isEqualTo(object.toString().length());
			Assertions.assertThat(report.compactString()).isEqualTo(object.toString());
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    " + object.toString()
			);
		}

		@Example
		void stringWithHeaderAndAppendix() {
			ValueReport.ReportingFormatFinder finder = value -> new NullReportingFormat() {
				@Override
				public Optional<String> sampleTypeHeader() {
					return Optional.of("java.lang.String:");
				}
			};

			ValueReport report = ValueReport.of("this is a string", finder);

			Assertions.assertThat(report.compactLength()).isEqualTo(17 + 18);
			Assertions.assertThat(report.compactString()).isEqualTo("java.lang.String:\"this is a string\"");
			report.report(lineReporter, 2, "#");
			assertThat(lineReporter.lines).containsSequence(
				"    java.lang.String:\"this is a string\"#"
			);
		}

		@Group
		class Collections {

			@Example
			void fitsInOneLine() {
				List<String> list = asList("string 1", "string 2", "string 3", "string 4");
				ValueReport report = ValueReport.of(list);

				String expectedCompact = "[\"string 1\", \"string 2\", \"string 3\", \"string 4\"]";
				Assertions.assertThat(report.compactLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.compactString()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    [",
					"      \"string 1\", \"string 2\", \"string 3\", \"string 4\"",
					"    ]"
				);
			}

			@Example
			void withHeaderAndAppendix() {
				NullReportingFormat collectionFormat = new NullReportingFormat() {
					@Override
					public boolean applyToType(final Class<?> valueClass) {
						return List.class.isAssignableFrom(valueClass);
					}

					@Override
					public Optional<String> sampleTypeHeader() {
						return Optional.of("java.lang.List");
					}
				};
				ValueReport.ReportingFormatFinder finder = formatFinder(collectionFormat);

				List<String> list = asList("string 1", "string 2");
				ValueReport report = ValueReport.of(list, finder);

				String expectedCompact = "java.lang.List[\"string 1\", \"string 2\"]";
				Assertions.assertThat(report.compactLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.compactString()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, ",");
				assertThat(lineReporter.lines).containsSequence(
					"    java.lang.List[",
					"      \"string 1\", \"string 2\"",
					"    ],"
				);
			}

			@Example
			void listOfLists() {
				List<List<String>> list = asList(
					asList("string 1", "string 2", "string 3", "string 4"),
					asList("string 5", "string 6", "string 7", "string 8"),
					asList(
						"a long string a long string a long string a long string a long string a long string",
						"a long string a long string a long string a long string a long string a long string"
					)
				);
				ValueReport report = ValueReport.of(list);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    [",
					"      [\"string 1\", \"string 2\", \"string 3\", \"string 4\"], [\"string 5\", \"string 6\", \"string 7\", \"string 8\"], ",
					"      [",
					"        \"a long string a long string a long string a long string a long string a long string\", ",
					"        \"a long string a long string a long string a long string a long string a long string\"",
					"      ]",
					"    ]"
				);
			}

		}

		private ValueReport.ReportingFormatFinder formatFinder(SampleReportingFormat... formats) {
			return value -> Arrays.stream(formats)
								  .filter(format -> format.applyToType(value.getClass()))
								  .findFirst().orElse(new NullReportingFormat());
		}

	}
}
