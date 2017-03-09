package net.jqwik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.mockito.Matchers.*;

import org.junit.platform.engine.*;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.mockito.InOrder;
import org.mockito.Mockito;

import net.jqwik.api.Example;
import net.jqwik.discovery.JqwikClassTestDescriptor;
import net.jqwik.discovery.JqwikExampleTestDescriptor;

class JqwikTestEngineTests {

	@Example
	void runTestFromPackage() {
		JqwikTestEngine testEngine = new JqwikTestEngine();
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.p1")).build();
		UniqueId uniqueId = UniqueId.forEngine(testEngine.getId());

		TestDescriptor engineDescriptor = testEngine.discover(discoveryRequest, uniqueId);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);

		EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder,
				discoveryRequest.getConfigurationParameters());
		testEngine.execute(executionRequest);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isA(JqwikClassTestDescriptor.class));
		events.verify(eventRecorder).executionStarted(isA(JqwikExampleTestDescriptor.class));
		events.verify(eventRecorder).executionFinished(isA(JqwikExampleTestDescriptor.class), eq(TestExecutionResult.successful()));
		events.verify(eventRecorder).executionFinished(isA(JqwikClassTestDescriptor.class), eq(TestExecutionResult.successful()));
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

}
