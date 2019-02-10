package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;

import examples.packageWithDisabledTests.*;
import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.testkit.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.testkit.engine.EventConditions.*;

class JqwikIntegrationTests {

	private final JqwikTestEngine testEngine;

	JqwikIntegrationTests() {
		testEngine = new JqwikTestEngine(this::configuration);
	}

	private JqwikConfiguration configuration() {
		return new JqwikConfiguration() {
			@Override
			public PropertyDefaultValues propertyDefaultValues() {
				return PropertyDefaultValues.with(1000, 5, AfterFailureMode.PREVIOUS_SEED);
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
				return false;
			}
		};
	}

	@Example
	void runTestsFromRootDir() {
		Set<Path> classpathRoots = JqwikReflectionSupport.getAllClasspathRootDirectories();
		ClasspathRootSelector[] classpathRootSelectors = selectClasspathRoots(classpathRoots)
															 .toArray(new ClasspathRootSelector[classpathRoots.size()]);
		Events events = EngineTestKit
							.engine(testEngine)
							.selectors(classpathRootSelectors)
							.filters(PackageNameFilter.includePackageNames("examples.packageWithSingleContainer"))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromPackage() {
		Events events = EngineTestKit
							.engine(testEngine)
							.selectors(selectPackage("examples.packageWithSingleContainer"))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	@Example
	void runTestsFromClass() {
		Events events = EngineTestKit
							.engine(testEngine)
							.selectors(selectClass(SimpleExampleTests.class))
							.execute()
							.all();

		assertSimpleExampleTests(events);
	}

	private void assertSimpleExampleTests(Events events) {
		events.assertEventsMatchExactly(
			event(engine(), started()),
			event(container(SimpleExampleTests.class), started()),
			event(test("failing"), started()),
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
							.engine(testEngine)
							.selectors(selectMethod(SimpleExampleTests.class, "succeeding"))
							.execute()
							.all();

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
							.engine(testEngine)
							.selectors(selectPackage("examples.packageWithSeveralContainers"))
							.execute()
							.all();

		assertAllEventsMatch(
			events,
			event(engine(), started()),

			// ExampleTests
			event(container(ExampleTests.class), started()),
			event(test("succeeding"), started()),
			event(test("succeeding"), finishedSuccessfully()),
			event(test("failing"), started()),
			event(test("failing"), finishedWithFailure()),
			event(container(ExampleTests.class), finishedSuccessfully()),

			// PropertyTests
			event(container(PropertyTests.class), started()),
			event(test("isTrue"), started()),
			event(test("isTrue"), finishedSuccessfully()),
			event(test("isFalse"), started()),
			event(test("isFalse"), finishedWithFailure()),
			event(test("withEverything"), started()),
			event(test("withEverything"), finishedSuccessfully()),
			event(test("allNumbersAreZero"), started()),
			event(test("allNumbersAreZero"), finishedWithFailure()),
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
							.engine(testEngine)
							.selectors(selectPackage("examples.packageWithDisabledTests"))
							.execute()
							.all();

		assertAllEventsMatch(
			events,
			event(container(DisabledTests.class), started()),
			event(test("disabledSuccess"), skippedWithReason("a reason")),
			event(test("disabledFailure"), skippedWithReason(r -> r.startsWith("@Disabled:"))),
			event(container(DisabledTests.class), finishedSuccessfully()),
			event(container(DisabledTests.DisabledGroup.class), skippedWithReason(r -> r.startsWith("@Disabled:")))

		);

	}

	@SafeVarargs
	private static void assertAllEventsMatch(Events events, Condition<? super Event>... conditions) {
		for (int i = 0; i < conditions.length; i++) {
			assertOneMatches(events.list(), conditions[i]);
		}
	}

	private static void assertOneMatches(List<Event> events, Condition<? super Event> condition) {
		if (events.stream().anyMatch(condition::matches)) {
			return;
		}
		StringBuffer eventsList = new StringBuffer();
		for (Event event : events) {
			eventsList.append(String.format("%s%n", event));
		}
		Assertions.fail(String.format("No event in %n%s matches %s", eventsList, condition));
	}

}
