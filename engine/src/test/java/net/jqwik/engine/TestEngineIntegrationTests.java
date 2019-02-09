package net.jqwik.engine;

import java.util.*;

import examples.packageWithDisabledTests.*;
import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import org.junit.platform.engine.*;
import org.junit.platform.launcher.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.engine.matchers.*;
import net.jqwik.engine.recording.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.*;
import static org.mockito.Mockito.*;

// TODO: Migrate to JqwikTestKitTests
class TestEngineIntegrationTests {

	private JqwikTestEngine testEngine;
	private EngineExecutionListener eventRecorder;
	private UniqueId engineId;

	private TestEngineIntegrationTests() {
		testEngine = new JqwikTestEngine(this::configuration);
		eventRecorder = Mockito.mock(EngineExecutionListener.class);
		engineId = UniqueId.forEngine(testEngine.getId());
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
				return true;
			}
		};
	}

	@Example
	void runTestsFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isClassDescriptorFor(SimpleExampleTests.class),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runMixedExamples() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);

		// ExampleTests
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(ExampleTests.class));
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "failing"),
			TestExecutionResultMatchers.isFailed()
		);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(ExampleTests.class, "succeeding"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isClassDescriptorFor(ExampleTests.class),
			TestExecutionResultMatchers.isSuccessful()
		);

		// PropertyTests
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(PropertyTests.class));
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isFalse"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isFalse"),
			TestExecutionResultMatchers.isFailed()
		);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isTrue"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "isTrue"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"),
			TestExecutionResultMatchers.isFailed()
		);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "withEverything"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(PropertyTests.class, "withEverything"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isClassDescriptorFor(PropertyTests.class),
			TestExecutionResultMatchers.isSuccessful()
		);

		// MixedTests
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(MixedTests.class));
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "anExample"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "anExample"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "aProperty"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(MixedTests.class, "aProperty"),
			TestExecutionResultMatchers.isSuccessful()
		);
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isClassDescriptorFor(MixedTests.class),
			TestExecutionResultMatchers.isSuccessful()
		);

		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runDisabledTests() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithDisabledTests")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);

		// DisabledTests
		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isClassDescriptorFor(DisabledTests.class));

		verify(eventRecorder).executionStarted(TestDescriptorMatchers.isPropertyDescriptorFor(DisabledTests.class, "success"));
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isPropertyDescriptorFor(DisabledTests.class, "success"),
			TestExecutionResultMatchers.isSuccessful()
		);

		verify(eventRecorder).executionSkipped(
			TestDescriptorMatchers.isPropertyDescriptorFor(DisabledTests.class, "disabledSuccess"),
			eq("a reason")
		);
		verify(eventRecorder).executionSkipped(
			TestDescriptorMatchers.isPropertyDescriptorFor(DisabledTests.class, "disabledFailure"),
			startsWith("@Disabled:")
		);
		verify(eventRecorder).executionSkipped(
			TestDescriptorMatchers.isClassDescriptorFor(DisabledTests.DisabledGroup.class),
			startsWith("@Disabled:")
		);
		verify(eventRecorder).executionFinished(
			TestDescriptorMatchers.isClassDescriptorFor(DisabledTests.class),
			TestExecutionResultMatchers.isSuccessful()
		);

		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private TestDescriptor runTests(LauncherDiscoveryRequest discoveryRequest) {
		TestDescriptor engineDescriptor = testEngine.discover(discoveryRequest, engineId);
		ExecutionRequest executionRequest = new ExecutionRequest(
			engineDescriptor, eventRecorder,
			discoveryRequest.getConfigurationParameters()
		);
		testEngine.execute(executionRequest);
		return engineDescriptor;
	}

}
