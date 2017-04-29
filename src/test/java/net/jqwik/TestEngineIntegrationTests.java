package net.jqwik;

import static net.jqwik.matchers.MockitoMatchers.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.launcher.*;
import org.mockito.*;

import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import net.jqwik.api.*;
import net.jqwik.recording.*;
import net.jqwik.support.*;

class TestEngineIntegrationTests {

	private JqwikTestEngine testEngine;
	private EngineExecutionListener eventRecorder;
	private UniqueId engineId;

	private TestEngineIntegrationTests() throws IOException {
		testEngine = new JqwikTestEngine(new TestEngineConfiguration() {
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

		});
		eventRecorder = Mockito.mock(EngineExecutionListener.class);
		engineId = UniqueId.forEngine(testEngine.getId());
	}

	@Example
	void runTestsFromRootDir() {
		LauncherDiscoveryRequest discoveryRequest = request()
				.selectors(selectClasspathRoots(JqwikReflectionSupport.getAllClasspathRootDirectories()))
				.filters(PackageNameFilter.includePackageNames("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(SimpleExampleTests.class)).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runMixedExamples() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);

		// ExampleTests
		verify(eventRecorder).executionStarted(isClassDescriptorFor(ExampleTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(ExampleTests.class), isSuccessful());

		// PropertyTests
		verify(eventRecorder).executionStarted(isClassDescriptorFor(PropertyTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(PropertyTests.class, "isFalse"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(PropertyTests.class, "isFalse"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(PropertyTests.class, "isTrue"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(PropertyTests.class, "isTrue"), isSuccessful());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(PropertyTests.class, "allNumbersAreZero"), isFailed());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(PropertyTests.class, "withEverything"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(PropertyTests.class, "withEverything"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(PropertyTests.class), isSuccessful());

		// MixedTests
		verify(eventRecorder).executionStarted(isClassDescriptorFor(MixedTests.class));
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(MixedTests.class, "anExample"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(MixedTests.class, "anExample"), isSuccessful());
		verify(eventRecorder).executionStarted(isPropertyDescriptorFor(MixedTests.class, "aProperty"));
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(MixedTests.class, "aProperty"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(MixedTests.class), isSuccessful());

		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private TestDescriptor runTests(LauncherDiscoveryRequest discoveryRequest) {
		TestDescriptor engineDescriptor = testEngine.discover(discoveryRequest, engineId);
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder,
				discoveryRequest.getConfigurationParameters());
		testEngine.execute(executionRequest);
		return engineDescriptor;
	}

}
