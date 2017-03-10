package net.jqwik;

import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import net.jqwik.discovery.JqwikClassTestDescriptor;
import net.jqwik.discovery.JqwikExampleTestDescriptor;
import net.jqwik.execution.JqwikExecutor;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.*;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.mockito.InOrder;
import org.mockito.Mockito;

import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;

import java.lang.reflect.Method;

class ExecutionTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	@Example
	void executeEmptyEngine() {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(engineId);

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void executeEmptyClassWithingEngine() {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(engineId);
		JqwikClassTestDescriptor containerDescriptor = new JqwikClassTestDescriptor(ContainerClass.class, engineDescriptor);
		engineDescriptor.addChild(containerDescriptor);

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(containerDescriptor);
		events.verify(eventRecorder).executionFinished(containerDescriptor, TestExecutionResult.successful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void executeClassWithSingleTestWithinEngine() throws NoSuchMethodException {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(engineId);

		JqwikClassTestDescriptor containerDescriptor = new JqwikClassTestDescriptor(ContainerClass.class, engineDescriptor);
		engineDescriptor.addChild(containerDescriptor);

		Method succeedingMethod = ContainerClass.class.getDeclaredMethod("succeeding");
		JqwikExampleTestDescriptor succeedingDescriptor = new JqwikExampleTestDescriptor(succeedingMethod, ContainerClass.class, containerDescriptor);
		containerDescriptor.addChild(succeedingDescriptor);

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(containerDescriptor);
		events.verify(eventRecorder).executionStarted(succeedingDescriptor);
		events.verify(eventRecorder).executionFinished(succeedingDescriptor, TestExecutionResult.successful());
		events.verify(eventRecorder).executionFinished(containerDescriptor, TestExecutionResult.successful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private void executeTests(TestDescriptor engineDescriptor) {
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder, null);
		new JqwikExecutor().execute(executionRequest, engineDescriptor);
	}

	private static class ContainerClass {
		@Example
		public void succeeding() {}

		@Example
		public void failing() {
			fail("failing");
		}
	}
}
