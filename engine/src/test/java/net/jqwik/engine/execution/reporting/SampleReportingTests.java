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

		private final Collection<SampleReportingFormat> sampleReportingFormats = SampleReportingFormats.getReportingFormats();

		@Example
		void sampleReporterReportsParametersIndividually() {
			Map<String, Object> reports = new LinkedHashMap<>();
			reports.put("aString", "this is a string");
			reports.put("aNumber", 42);
			SampleReporter sampleReporter = new SampleReporter("Headline", reports, sampleReportingFormats);

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
			Map<String, Object> reports = new LinkedHashMap<>();
			reports.put("aString", "this is a string");
			SampleReporter sampleReporter = new SampleReporter(null, reports, sampleReportingFormats);

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
			Map<String, Object> reports = new LinkedHashMap<>();
			reports.put("aVeryLongString", parameterValue);
			SampleReporter sampleReporter = new SampleReporter("Headline", reports, sampleReportingFormats);

			sampleReporter.reportTo(lineReporter);

			assertThat(lineReporter.lines).containsSequence(
					"Headline",
					"--------",
					"  aVeryLongString:",
					String.format("    \"%s\"", parameterValue)
			);
		}

		@Example
		void sameObjectDifferentParameter() {

			Object anObject = new Object() {
				@Override
				public String toString() {
					return "an object";
				}
			};

			List<Object> listOfObject = asList(anObject);

			Map<String, Object> reports = new LinkedHashMap<>();
			reports.put("list1", listOfObject);
			reports.put("list2", listOfObject);

			SampleReporter sampleReporter = new SampleReporter("Headline", reports, sampleReportingFormats);

			sampleReporter.reportTo(lineReporter);

			assertThat(lineReporter.lines).containsSequence(
					"Headline",
					"--------",
					"  list1: [an object]",
					"  list2: [an object]"
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
					"    " + object
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
		void optionalNotEmpty() {
			ValueReport report = ValueReport.of(Optional.of("not empty"));

			String expectedCompact = "Optional[\"not empty\"]";
			Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
					"    Optional[",
					"      \"not empty\"",
					"    ]"
			);
		}

		@Example
		void optionalEmpty() {
			ValueReport report = ValueReport.of(Optional.empty());

			String expectedCompact = "Optional[null]";
			Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);
			report.report(lineReporter, 2, "");
			assertThat(lineReporter.lines).containsSequence(
					"    Optional[",
					"      null",
					"    ]"
			);
		}

		@Example
		void stringWithLabelAndAppendix() {
			SampleReportingFormat format = new NullReportingFormat() {
				@Override
				public Optional<String> label(Object value) {
					return Optional.of("java.lang.String:");
				}
			};

			ValueReport report = ValueReport.of("this is a string", asList(format));

			Assertions.assertThat(report.singleLineLength()).isEqualTo(17 + 18);
			Assertions.assertThat(report.singleLineReport()).isEqualTo("java.lang.String:\"this is a string\"");
			report.report(lineReporter, 2, "#");
			assertThat(lineReporter.lines).containsSequence(
					"    java.lang.String:",
					"      \"this is a string\"#"
			);
		}

		@Group
		class CircularDependencies {

			@SuppressWarnings("CollectionAddedToSelf")
			@Example
			void listWithItselfInIt() {
				List<Object> aList = new ArrayList<>();
				aList.add(42);
				aList.add(aList);

				ValueReport report = ValueReport.of(aList);
				Assertions.assertThat(report.singleLineReport()).startsWith("[42, circular-dependency<java.util.ArrayList@");
			}

			@Example
			void doNotReportCircularDependencyIfObjectIsNotContainedInItself() {
				List<Object> list42 = new ArrayList<>();
				list42.add(42);

				List<List<Object>> listOfList = new ArrayList<>();
				listOfList.add(list42);

				Map<String, Object> map = new LinkedHashMap<>();
				map.put("list1", listOfList);
				map.put("list2", list42);

				ValueReport report = ValueReport.of(map);
				report.report(lineReporter, 0, "");
				assertThat(lineReporter.lines).containsSequence(
						"{",
						"  \"list1\"=[[42]],",
						"  \"list2\"=[42]",
						"}"
				);
			}

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

				List<String> list = asList("string 1", "string 2");
				ValueReport report = ValueReport.of(list, asList(collectionFormat));

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
				Map<String, Integer> map = new LinkedHashMap<>();
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

				Map<String, Integer> map = new LinkedHashMap<>();
				map.put("key1", 1);
				ValueReport report = ValueReport.of(map, asList(collectionFormat));

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
				Map<String, List<String>> map = new LinkedHashMap<>();
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

				Tuple.Tuple2<String, Integer> tuple = Tuple.of("one", 2, 3.0);
				ValueReport report = ValueReport.of(tuple, asList(tupleFormat));

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
			void charArray() {
				char[] array = {'j', 'A', 'v', 'a'};
				ValueReport report = ValueReport.of(array);

				String expectedCompact = "char[] [j, A, v, a]";
				Assertions.assertThat(report.singleLineLength()).isEqualTo(expectedCompact.length());
				Assertions.assertThat(report.singleLineReport()).isEqualTo(expectedCompact);

				report.report(lineReporter, 2, "");
				assertThat(lineReporter.lines).containsSequence(
					"    char[] [",
					"      j, A, v, a",
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

			@SuppressWarnings("unchecked")
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
				Arbitrary<Stream<Integer>> streams = Arbitraries.just(1).stream().ofSize(3);
				Stream<Integer> stream = streams.generator(10, true).next(random).value();

				ValueReport report = ValueReport.of(stream);
				Assertions.assertThat(report.singleLineReport()).isEqualTo("Stream.of [1, 1, 1]");
			}

			@Example
			void generatedStreamsCanBeReportedWithoutEvaluation(@ForAll Random random) {
				Arbitrary<Stream<Integer>> streams = Arbitraries.just(1).stream().ofSize(3);
				Stream<Integer> stream = streams.generator(10, true).next(random).value();

				ValueReport report = ValueReport.of(stream);
				assertThat(stream.count()).isEqualTo(3); // Force evaluation
				Assertions.assertThat(report.singleLineReport()).isEqualTo("Stream.of [1, 1, 1]");
			}

		}
	}
}
