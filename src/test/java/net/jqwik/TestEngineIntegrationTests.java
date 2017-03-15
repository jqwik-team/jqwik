package net.jqwik;

import static net.jqwik.matchers.MockitoMatchers.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.mockito.Mockito.verify;

import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.mockito.Mockito;

import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;

class TestEngineIntegrationTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	@Example
	void runTestsFromRootDir() {
		LauncherDiscoveryRequest discoveryRequest = request()
				.selectors(selectClasspathRoots(JqwikReflectionSupport.getAllClasspathRootDirectories()))
				.filters(PackageNameFilter.includePackageNames("examples.packageWithSingleContainer"))
				.build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(SimpleExampleTests.class)).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "failing"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = runTests(discoveryRequest);

		verify(eventRecorder).executionStarted(engineDescriptor);
		verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
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
