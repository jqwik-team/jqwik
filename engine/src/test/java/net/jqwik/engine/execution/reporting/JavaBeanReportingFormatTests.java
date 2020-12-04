package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class JavaBeanReportingFormatTests {

	private final LineReporterStub lineReporter = new LineReporterStub();

	@Example
	void reportWithEmbeddedBean() {
		SampleReportingFormat myObjectFormat = new JavaBeanReportingFormat() {
			@Override
			protected Collection<Class<?>> beanTypes() {
				return Arrays.asList(MyBean.class, OtherBean.class);
			}

			@Override
			protected Collection<String> excludeProperties() {
				return Collections.singleton("toIgnore");
			}
		};

		ValueReport.ReportingFormatFinder finder = formatFinder(myObjectFormat);

		ValueReport report = ValueReport.of(new MyBean(), finder);

		report.report(lineReporter, 2, ",");
		assertThat(lineReporter.lines).containsSequence(
				"    MyBean{",
				"      age=17,",
				"      name=\"name\",",
				"      notEmpty=Optional[not empty],",
				"      young=true,",
				"      yourObjects=[OtherBean{hallo=\"hello\"}]",
				"    },"
		);
	}

	@Example
	void reportNulls() {
		SampleReportingFormat myObjectFormat = new JavaBeanReportingFormat() {
			@Override
			protected Collection<Class<?>> beanTypes() {
				return Arrays.asList(MyBean.class);
			}

			@Override
			protected boolean reportNulls() {
				return true;
			}

			@Override
			protected Collection<String> excludeProperties() {
				return Arrays.asList("yourObjects", "toIgnore");
			}
		};

		ValueReport.ReportingFormatFinder finder = formatFinder(myObjectFormat);

		ValueReport report = ValueReport.of(new MyBean(), finder);

		report.report(lineReporter, 2, ",");
		assertThat(lineReporter.lines).containsSequence(
				"    MyBean{",
				"      age=17,",
				"      doNotShowEmpty=Optional.empty,",
				"      doNotShowNull=null,",
				"      name=\"name\",",
				"      notEmpty=Optional[not empty],",
				"      young=true",
				"    },"
		);
	}

	@Example
	void sortPropertiesToFront() {
		SampleReportingFormat myObjectFormat = new JavaBeanReportingFormat() {
			@Override
			protected Collection<Class<?>> beanTypes() {
				return Arrays.asList(MyBean.class);
			}

			@Override
			protected List<String> sortProperties(List<String> properties) {
				ArrayList<String> sorted = new ArrayList<>(properties);
				sorted.remove("young");
				sorted.add(0, "young");
				sorted.remove("notEmpty");
				sorted.add(1, "notEmpty");
				sorted.add(2, "nonExistant");
				return sorted;
			}

			@Override
			protected Collection<String> excludeProperties() {
				return Arrays.asList("yourObjects", "toIgnore");
			}
		};

		ValueReport.ReportingFormatFinder finder = formatFinder(myObjectFormat);

		ValueReport report = ValueReport.of(new MyBean(), finder);

		report.report(lineReporter, 2, ",");
		assertThat(lineReporter.lines).containsSequence(
				"    MyBean{",
				"      young=true,",
				"      notEmpty=Optional[not empty],",
				"      age=17,",
				"      name=\"name\"",
				"    },"
		);
	}

	private ValueReport.ReportingFormatFinder formatFinder(SampleReportingFormat... formats) {
		return value -> Arrays.stream(formats)
							  .filter(format -> format.appliesTo(value))
							  .findFirst().orElse(new NullReportingFormat());
	}

	private static class OtherBean {
		public String getHallo() {
			return "hello";
		}

		public String getToIgnore() {
			return "should be ignored";
		}
	}

	private static class MyBean {

		public static String getStaticToIgnore() {
			return "do not show";
		}

		public int getAge() {
			return 17;
		}

		public String getName() {
			return "name";
		}

		public boolean isYoung() {
			return true;
		}

		public String getDoNotShowNull() {
			return null;
		}

		public List<OtherBean> getYourObjects() {
			return java.util.Collections.singletonList(new OtherBean());
		}

		public String get() {
			return "hallo";
		}

		public boolean is() {
			return true;
		}

		public String getWithParameter(int anInt) {
			return "should not show up";
		}

		public Optional<String> getNotEmpty() {
			return Optional.of("not empty");
		}

		public Optional<String> getDoNotShowEmpty() {
			return Optional.empty();
		}

		public String getToIgnore() {
			return "should be ignored";
		}
	}

}
