package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;

import examples.packageWithDisabledTests.*;
import examples.packageWithErrors.*;
import examples.packageWithFailings.*;
import examples.packageWithInheritance.*;
import examples.packageWithProperties.*;
import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.engine.reporting.*;
import org.junit.platform.testkit.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.testkit.engine.Event.*;
import static org.junit.platform.testkit.engine.EventConditions.*;
import static org.junit.platform.testkit.engine.EventType.*;

@SuppressLogging
class JqwikIntegrationTests {

	private JqwikConfiguration configuration(final boolean reportOnlyFailures) {
		return new JqwikConfiguration() {
			@Override
			public PropertyAttributesDefaults propertyDefaultValues() {
				return TestHelper.propertyAttributesDefaults();
			}

			@Override
			public TestEngineConfiguration testEngineConfiguration() {
				return new TestEngineConfiguration() {
					@Override
					public TestRunRecorder recorder() {
						return testRun -> {
						};
					}

					@Override
					public TestRunData previousRun() {
						return new TestRunData();
					}

					@Override
					public Set<UniqueId> previousFailures() {
						return Collections.emptySet();
					}

				};
			}

			@Override
			public boolean useJunitPlatformReporter() {
				return true;
			}

			@Override
			public boolean reportOnlyFailures() {
				return reportOnlyFailures;
			}
		};
	}

	private JqwikTestEngine createTestEngine(final boolean reportOnlyFailures) {
		return new JqwikTestEngine(unusedConfigurationProperties -> configuration(reportOnlyFailures));
	}

	private JqwikTestEngine createDefaultTestEngine() {
		return createTestEngine(true);
	}

	@Example
	void runTestsFromRootDir() {
		Set<Path> classpathRoots = JqwikReflectionSupport.getAllClasspathRootDirectories();
		ClasspathRootSelector[] classpathRootSelectors = selectClasspathRoots(classpathRoots)
			.toArray(new ClasspathRootSelector[classpathRoots.size()]);
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(classpathRootSelectors)
			.filters((Filter<?>) PackageNameFilter.includePackageNames("examples.packageWithSingleContainer"))
			.execute()
			.allEvents();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromPackage() {
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectPackage("examples.packageWithSingleContainer"))
			.execute()
			.allEvents();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromClass() {
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectClass(SimpleExampleTests.class))
			.execute()
			.allEvents();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromClassWithInheritance() {
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectClass(ContainerWithInheritance.class))
			.execute()
			.allEvents();

		events.assertEventsMatchLoosely(
			event(engine(), started()),
			event(container(ContainerWithInheritance.class), started()),
			event(test("example"), finishedSuccessfully()),
			event(test("exampleToInherit"), finishedSuccessfully()),
			event(test("exampleToOverride"), finishedSuccessfully()),
			event(test("exampleToOverrideFromInterface"), finishedSuccessfully()),
			event(test("exampleToInheritFromInterface"), finishedSuccessfully()),
			event(container(AbstractContainer.ContainerInAbstractClass.class), started()),
			event(test("innerExampleToInherit"), finishedSuccessfully()),
			event(container(AbstractContainer.ContainerInAbstractClass.class), finishedSuccessfully()),
			event(container(ContainerWithInheritance.class), finishedSuccessfully()),
			event(engine(), finishedSuccessfully())
		);
	}

	@Example
	void failingConstructorFailsTests() {
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectClass(ContainerWithFailingConstructor.class))
			.execute()
			.allEvents();

		events.assertEventsMatchExactly(
			event(engine(), started()),
			event(container(ContainerWithFailingConstructor.class), started()),
			event(test("success"), started()),
			event(test("success"), finishedWithFailure()),
			event(container(ContainerWithFailingConstructor.class), finishedSuccessfully()),
			event(engine(), finishedSuccessfully())
		);
	}

	private void assertSimpleExampleTests(Events events) {
		events.assertEventsMatchExactly(
			event(engine(), started()),
			event(container(SimpleExampleTests.class), started()),
			event(test("failing"), started()),
			event(reported("SimpleExampleTests:failing")),
			event(test("failing"), finishedWithFailure()),
			event(test("withJupiterAnnotation"), skippedWithReason(s -> true)),
			event(test("staticExample"), skippedWithReason(s -> true)),
			event(test("succeeding"), started()),
			event(test("succeeding"), finishedSuccessfully()),
			event(container(SimpleExampleTests.class), finishedSuccessfully()),
			event(engine(), finishedSuccessfully())
		);
	}

	@Example
	void runTestsFromMethod() {
		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectMethod(SimpleExampleTests.class, "succeeding"))
			.execute()
			.allEvents();

		events.assertEventsMatchExactly(
			event(engine(), started()),
			event(container(SimpleExampleTests.class), started()),
			event(test("succeeding"), started()),
			event(test("succeeding"), finishedSuccessfully()),
			event(container(SimpleExampleTests.class), finishedSuccessfully()),
			event(engine(), finishedSuccessfully())
		);
	}

	@Example
	void runMixedExamples() {

		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectPackage("examples.packageWithSeveralContainers"))
			.execute()
			.allEvents();

		// Order of classes is platform dependent :-(
		// events.assertEventsMatchLooselyInOrder(
		events.assertEventsMatchLoosely(
			event(engine(), started()),

			// ExampleTests
			event(container(ExampleTests.class), started()),
			event(test("succeeding"), started()),
			event(test("succeeding"), finishedSuccessfully()),
			event(test("failingSimple"), started()),
			event(test("failingSimple"), finishedWithFailure()),
			event(container(ExampleTests.class), finishedSuccessfully()),

			// PropertyTests
			event(container(PropertyTests.class), started()),
			event(test("isTrue"), started()),
			event(test("isTrue"), finishedSuccessfully()),
			event(test("allNumbersAreZero"), started()),
			event(test("allNumbersAreZero"), finishedWithFailure()),
			event(test("withEverything"), started()),
			event(test("withEverything"), finishedSuccessfully()),
			event(test("isFalse"), started()),
			event(test("isFalse"), finishedWithFailure()),
			event(container(PropertyTests.class), finishedSuccessfully()),

			// MixedTests
			event(container(MixedTests.class), started()),
			event(test("anExample"), started()),
			event(test("anExample"), finishedSuccessfully()),
			event(test("aProperty"), started()),
			event(test("aProperty"), finishedSuccessfully()),
			event(container(MixedTests.class), finishedSuccessfully()),

			event(engine(), finishedSuccessfully())
		);

	}

	@Example
	void runDisabledTests() {

		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectPackage("examples.packageWithDisabledTests"))
			.execute()
			.allEvents();

		events.assertEventsMatchLooselyInOrder(
			event(container(DisabledTests.class), started()),
			event(test("disabledFailure"), skippedWithReason(r -> r.startsWith("@Disabled:"))),
			event(container(DisabledTests.class), finishedSuccessfully())
		);

		events.assertEventsMatchLooselyInOrder(
			event(container(DisabledTests.class), started()),
			event(test("disabledSuccess"), skippedWithReason("a reason")),
			event(container(DisabledTests.class), finishedSuccessfully())
		);

		events.assertEventsMatchLooselyInOrder(
			event(container(DisabledTests.class), started()),
			event(container(DisabledTests.DisabledGroup.class), skippedWithReason(r -> r.startsWith("@Disabled:"))),
			event(container(DisabledTests.class), finishedSuccessfully())
		);

	}

	@Example
	void statisticsAreBeingReported() {

		Events events = EngineTestKit
			.engine(createDefaultTestEngine())
			.selectors(selectClass(ContainerWithStatistics.class))
			.execute()
			.allEvents();

		events.assertEventsMatchLooselyInOrder(
			event(container(ContainerWithStatistics.class), started()),
			event(test("propertyWithStatistics"), reported("[ContainerWithStatistics:propertyWithStatistics] (100) statistics")),
			event(test("propertyWithStatistics"), finishedSuccessfully()),
			event(container(ContainerWithStatistics.class), finishedSuccessfully())
		);

	}

	@Example
	void outOfMemoryErrorIsPropagatedToTop() {
		Assertions.assertThatThrownBy(
			() -> EngineTestKit
				.engine(createDefaultTestEngine())
				.selectors(selectClass(ContainerWithOOME.class))
				.execute()
		).isInstanceOf(OutOfMemoryError.class);
	}

	@Group
	class Reporting {

		@Example
		void doNotReportSuccessfulExampleWithoutForAllParameter() {
			Events events = EngineTestKit
				.engine(createTestEngine(false))
				.selectors(selectMethod(TestsForReporting.class, "succeeding"))
				.execute()
				.allEvents();

			events.assertThatEvents().noneMatch(event -> event.getType() == REPORTING_ENTRY_PUBLISHED);
		}

		@Example
		void doReportFailingExample() {
			Events events = EngineTestKit
				.engine(createTestEngine(false))
				.selectors(selectMethod(TestsForReporting.class, "failing"))
				.execute()
				.allEvents();

			events.assertEventsMatchLoosely(
				reported("TestsForReporting:failing")
			);
		}

		@Example
		void doReportSuccessfulExampleWithForAllParameter() {
			Events events = EngineTestKit
				.engine(createTestEngine(false))
				.selectors(selectMethod(TestsForReporting.class, "succeedingWithForAll", String.class.getName()))
				.execute()
				.allEvents();

			events.assertEventsMatchLoosely(
				reported("TestsForReporting:succeedingWithForAll")
			);
		}

		@Example
		void doReportPropertyWithoutParameters() {
			Events events = EngineTestKit
				.engine(createTestEngine(false))
				.selectors(selectMethod(TestsForReporting.class, "succeedingPropertyWithoutParameters"))
				.execute()
				.allEvents();

			events.assertEventsMatchLoosely(
				reported("TestsForReporting:succeedingPropertyWithoutParameters")
			);
		}
	}

	private Condition<Event> reported(String key) {
		Condition<ReportEntry> condition = new Condition<ReportEntry>() {
			@Override
			public boolean matches(ReportEntry entry) {
				return entry.getKeyValuePairs().containsKey(key);
			}
		};
		return Assertions.allOf(
			type(REPORTING_ENTRY_PUBLISHED),
			new Condition<>(byPayload(ReportEntry.class, condition::matches), "event with result where %s", condition)
		);
	}

}
