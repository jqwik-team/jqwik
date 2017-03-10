package net.jqwik;

import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import org.junit.platform.engine.*;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.mockito.InOrder;
import org.mockito.Mockito;

import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;

class JqwikTestEngineIntegrationTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	@Example
	void runTestsFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);

		executeTests(discoveryRequest, engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "failing"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(SimpleExampleTests.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);

		executeTests(discoveryRequest, engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "failing"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "failing"), isFailed());
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void runTestsFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);

		executeTests(discoveryRequest, engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(SimpleExampleTests.class));
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(SimpleExampleTests.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(SimpleExampleTests.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private void executeTests(LauncherDiscoveryRequest discoveryRequest, TestDescriptor engineDescriptor) {
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder,
				discoveryRequest.getConfigurationParameters());
		testEngine.execute(executionRequest);
	}

	private TestDescriptor discoverTests(LauncherDiscoveryRequest discoveryRequest) {
		return testEngine.discover(discoveryRequest, engineId);
	}

}
