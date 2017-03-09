package net.jqwik;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.*;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.mockito.Mockito;

class JqwikTestEngineTests {

	@Test
	void runTestFromPackage() {
		JqwikTestEngine testEngine = new JqwikTestEngine();
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.p1")).build();
		UniqueId uniqueId = UniqueId.forEngine(testEngine.getId());

		TestDescriptor engineDescriptor = testEngine.discover(discoveryRequest, uniqueId);

		EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder,
				discoveryRequest.getConfigurationParameters());
		testEngine.execute(executionRequest);

		Mockito.verify(eventRecorder).executionStarted(engineDescriptor);
		Mockito.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

}
