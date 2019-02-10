package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;

import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.*;
import org.assertj.core.data.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.testkit.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.matchers.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.testkit.engine.EventConditions.*;
import static org.mockito.Mockito.*;

/**
 * Migrating tests from {@linkplain TestEngineIntegrationTests}
 */
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
	@Disabled("assertion does not work as expected")
	void runMixedExamples() {

		Events events = EngineTestKit
							.engine(testEngine)
							.selectors(selectPackage("examples.packageWithSeveralContainers"))
							.execute()
							.all();

		assertAllEventsMatch(
			events,
			event(engine(), started()),
			event(engine(), finishedSuccessfully())
		);

//		// ExampleTests
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(ExampleTests.class));
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "failing"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "failing"),
//			TestExecutionResultMatchers.isFailed()
//		);
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "succeeding"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "succeeding"),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isClassDescriptorFor(ExampleTests.class),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//
//		// PropertyTests
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(PropertyTests.class));
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isFalse"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isFalse"),
//			TestExecutionResultMatchers.isFailed()
//		);
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isTrue"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isTrue"),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"),
//			TestExecutionResultMatchers.isFailed()
//		);
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "withEverything"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "withEverything"),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isClassDescriptorFor(PropertyTests.class),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//
//		// MixedTests
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(MixedTests.class));
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "anExample"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "anExample"),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "aProperty"));
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "aProperty"),
//			TestExecutionResultMatchers.isSuccessful()
//		);
//		verify(eventRecorder).executionFinished(
//			TestDescriptorMatchers.isClassDescriptorFor(MixedTests.class),
//			TestExecutionResultMatchers.isSuccessful()
//		);
	}

	@SafeVarargs
	private static void assertAllEventsMatch(Events events, Condition<? super Event>... conditions) {
		// TODO: match conditions starting in the beginning one by one
		for (int i = 0; i < conditions.length; i++) {
			final int index = i;
			Condition<? super Event> condition = new Condition<Event>() {
				@Override
				public boolean matches(Event value) {
					return conditions[index].matches(value);
				}
			};
			Assertions.assertThat(events.list()).have(condition);
		}
	}

}
