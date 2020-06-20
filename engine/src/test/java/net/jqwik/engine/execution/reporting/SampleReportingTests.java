package net.jqwik.engine.execution.reporting;

import java.math.*;
import java.util.*;
import java.util.stream.*;

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

			Assertions.assertThat(report.singleLineLength()).isEqualTo(18);
			Assertions.assertThat(report.singleLineReport()).isEqualTo("\"this is a string\"");
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    \"this is a string\""
			);
		}

		@Example
		void number() {
			ValueReport report = ValueReport.of(BigDecimal.valueOf(2.5));

			Assertions.assertThat(report.singleLineLength()).isEqualTo(3);
			Assertions.assertThat(report.singleLineReport()).isEqualTo("2.5");
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    2.5"
			);
		}

		@Example
		void objects() {
			Object object = new Object();
			ValueReport report = ValueReport.of(object);

			Assertions.assertThat(report.singleLineLength()).isEqualTo(object.toString().length());
			Assertions.assertThat(report.singleLineReport()).isEqualTo(object.toString());
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    " + object.toString()
			);
		}

		@Example
		void objectWithMultilineToString() {
			Object object = new Object() {
				@Override
				public String toString() {
					return String.format("line1%nline2%nline3%n");
				}
			};
			ValueReport report = ValueReport.of(object);

			String expectedCompact = "line1 line2 line3";
			Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
			Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    line1",
				"    line2",
				"    line3"
			);
		}

		@Example
		void nullObject() {
			ValueReport report = ValueReport.of(null);

			String expectedCompact = "null";
			Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
			Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
				"    null"
			);
		}

		@Example
		void stringWithLabelAndAppendix() {
			ValueReport.ReportingFormatFinder finder = value -> new NullReportingFormat() {
				@Override
				public Optional<String> label(Object value) {
					return Optional.of("java.lang.String:");
				}
			};

			ValueReport report = ValueReport.of("this is a string", finder);

			Assertions.assertThat(report.singleLineLength()).isEqualTo(17 + 18);
			Assertions.assertThat(report.singleLineReport()).isEqualTo("java.lang.String:\"this is a string\"");
			report.report(lineReporter, 2, "#");
			assertThat(lineReporter.lines).containsSequence(
				"    java.lang.String:",
				"      \"this is a string\"#"
			);
		}

		@Group
		class Collections {

			@Example
			void fitsInOneLine() {
				List<String> list = asList("string 1", "string 2", "string 3", "string 4");
				ValueReport report = ValueReport.of(list);

				String expectedCompact = "[\"string 1\", \"string 2\", \"string 3\", \"string 4\"]";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    [",
					"      \"string 1\", \"string 2\", \"string 3\", \"string 4\"",
					"    ]"
				);
			}

			@Example
			void withLabelAndAppendix() {
				NullReportingFormat collectionFormat = new NullReportingFormat() {
					@Override
					public boolean appliesTo(final Object value) {
						return value instanceof List;
					}

					@Override
					public Optional<String> label(Object value) {
						return Optional.of("java.lang.List");
					}
				};
				ValueReport.ReportingFormatFinder finder = formatFinder(collectionFormat);

				List<String> list = asList("string 1", "string 2");
				ValueReport report = ValueReport.of(list, finder);

				String expectedCompact = "java.lang.List[\"string 1\", \"string 2\"]";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

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
					asList("string 5", "string 6", "string 7"),
					asList(
						"a long string a long string a long string a long string a long string a long string",
						"a long string a long string a long string a long string a long string a long string"
					)
				);
				ValueReport report = ValueReport.of(list);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    [",
					"      [\"string 1\", \"string 2\", \"string 3\", \"string 4\"], [\"string 5\", \"string 6\", \"string 7\"],",
					"      [",
					"        \"a long string a long string a long string a long string a long string a long string\",",
					"        \"a long string a long string a long string a long string a long string a long string\"",
					"      ]",
					"    ]"
				);
			}

		}

		@Group
		class Maps {
			@Example
			void fitsInOneLine() {
				Map<String, Integer> map = new HashMap<>();
				map.put("key1", 1);
				map.put("key2", 2);
				map.put("key3", 3);
				ValueReport report = ValueReport.of(map);

				String expectedCompact = "{\"key1\"=1, \"key2\"=2, \"key3\"=3}";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    {",
					"      \"key1\"=1,",
					"      \"key2\"=2,",
					"      \"key3\"=3",
					"    }"
				);
			}

			@Example
			void withLabelAndAppendix() {
				NullReportingFormat collectionFormat = new NullReportingFormat() {
					@Override
					public boolean appliesTo(final Object value) {
						return value instanceof Map;
					}

					@Override
					public Optional<String> label(Object value) {
						return Optional.of("java.lang.Map");
					}
				};
				ValueReport.ReportingFormatFinder finder = formatFinder(collectionFormat);

				Map<String, Integer> map = new HashMap<>();
				map.put("key1", 1);
				ValueReport report = ValueReport.of(map, finder);

				String expectedCompact = "java.lang.Map{\"key1\"=1}";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, ",");
				assertThat(lineReporter.lines).containsSequence(
					"    java.lang.Map{",
					"      \"key1\"=1",
					"    },"
				);
			}

			@Example
			void mapOfLists() {
				Map<String, List<String>> map = new HashMap<>();
				map.put(
					"list1",
					asList(
						"a long string a long string a long string a long string a long string a long string",
						"a long string a long string a long string a long string a long string a long string"
					)
				);
				map.put(
					"list2",
					asList("1", "2")
				);
				ValueReport report = ValueReport.of(map);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    {",
					"      \"list1\"=",
					"        [",
					"          \"a long string a long string a long string a long string a long string a long string\",",
					"          \"a long string a long string a long string a long string a long string a long string\"",
					"        ],",
					"      \"list2\"=[\"1\", \"2\"]",
					"    }"
				);
			}

		}

		@Group
		class Tuples {
			@Example
			void fitsInOneLine() {
				Tuple.Tuple2<String, Integer> tuple = Tuple.of("one", 2);
				ValueReport report = ValueReport.of(tuple);

				String expectedCompact = "(\"one\", 2)";
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    (",
					"      \"one\",",
					"      2",
					"    )"
				);
			}

			@Example
			void withLabelAndAppendix() {
				NullReportingFormat tupleFormat = new NullReportingFormat() {
					@Override
					public boolean appliesTo(final Object value) {
						return value instanceof Tuple;
					}

					@Override
					public Optional<String> label(Object value) {
						return Optional.of("Tuple");
					}
				};
				ValueReport.ReportingFormatFinder finder = formatFinder(tupleFormat);

				Tuple.Tuple2<String, Integer> tuple = Tuple.of("one", 2, 3.0);
				ValueReport report = ValueReport.of(tuple, finder);

				String expectedCompact = "Tuple(\"one\", 2, 3.0)";
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());

				report.report(lineReporter, 2, ",");
				assertThat(lineReporter.lines).containsSequence(
					"    Tuple(",
					"      \"one\",",
					"      2,",
					"      3.0",
					"    ),"
				);
			}

			@Example
			void tupleOfLists() {
				Tuple.Tuple2<String, List<String>> tuple = Tuple.of(
					"string",
					asList(
						"a long string a long string a long string a long string a long string a long string",
						"a long string a long string a long string a long string a long string a long string"
					)
				);
				ValueReport report = ValueReport.of(tuple);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    (",
					"      \"string\",",
					"      [",
					"        \"a long string a long string a long string a long string a long string a long string\",",
					"        \"a long string a long string a long string a long string a long string a long string\"",
					"      ]",
					"    )"
				);
			}

		}

		@Group
		class ArraysOfDifferentTypes {

			@Example
			void fitsInOneLine() {
				String[] array = new String[]{"string 1", "string 2", "string 3", "string 4"};
				ValueReport report = ValueReport.of(array);

				String expectedCompact = "String[] [\"string 1\", \"string 2\", \"string 3\", \"string 4\"]";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    String[] [",
					"      \"string 1\", \"string 2\", \"string 3\", \"string 4\"",
					"    ]"
				);
			}

			@Example
			void primitiveArray() {
				int[] array = new int[]{1, 2, 3, 4};
				ValueReport report = ValueReport.of(array);

				String expectedCompact = "int[] [1, 2, 3, 4]";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    int[] [",
					"      1, 2, 3, 4",
					"    ]"
				);
			}

			@Example
			void withAppendix() {
				String[] array = new String[]{"string 1", "string 2", "string 3", "string 4"};
				ValueReport report = ValueReport.of(array);

				report.report(lineReporter, 2, ",");
				assertThat(lineReporter.lines).containsSequence(
					"    String[] [",
					"      \"string 1\", \"string 2\", \"string 3\", \"string 4\"",
					"    ],"
				);
			}

			@Example
			void arrayOfLists() {
				List<String>[] list = new List[]{
					asList("string 1", "string 2", "string 3", "string 4"),
					asList("string 5", "string 6", "string 7"),
					asList(
						"a long string a long string a long string a long string a long string a long string",
						"a long string a long string a long string a long string a long string a long string"
					)
				};
				ValueReport report = ValueReport.of(list);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    java.util.List[] [",
					"      [\"string 1\", \"string 2\", \"string 3\", \"string 4\"], [\"string 5\", \"string 6\", \"string 7\"],",
					"      [",
					"        \"a long string a long string a long string a long string a long string a long string\",",
					"        \"a long string a long string a long string a long string a long string a long string\"",
					"      ]",
					"    ]"
				);
			}

		}

		@Group
		class Streams {

			@Example
			void generatedStreamsCanBeReportedBeforeEvaluation(@ForAll Random random) {
				Arbitrary<Stream<Integer>> streams = Arbitraries.constant(1).stream().ofSize(3);
				Stream<Integer> stream = streams.generator(10).next(random).value();

				ValueReport report = ValueReport.of(stream);
				Assertions.assertThat(report.singleLineReport()).isEqualTo("Stream.of [1, 1, 1]");
			}

			@Example
			void generatedStreamsCanBeReportedWithoutEvaluation(@ForAll Random random) {
				Arbitrary<Stream<Integer>> streams = Arbitraries.constant(1).stream().ofSize(3);
				Stream<Integer> stream = streams.generator(10).next(random).value();

				ValueReport report = ValueReport.of(stream);
				assertThat(stream.count()).isEqualTo(3); // Force evaluation
				Assertions.assertThat(report.singleLineReport()).isEqualTo("Stream.of [1, 1, 1]");
			}

		}

		private ValueReport.ReportingFormatFinder formatFinder(SampleReportingFormat... formats) {
			return value -> Arrays.stream(formats)
								  .filter(format -> format.appliesTo(value))
								  .findFirst().orElse(new NullReportingFormat());
		}

	}
}
