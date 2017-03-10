package net.jqwik;

import static net.jqwik.TestDescriptorBuilder.*;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.fail;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.InOrder;
import org.mockito.Mockito;

import net.jqwik.api.Example;
import net.jqwik.execution.JqwikExecutor;

class ExecutionTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);

	@Example
	void executeEmptyEngine() {
		TestDescriptor engineDescriptor = forEngine(testEngine).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void executeEngineWithEmptyClass() {
		TestDescriptor engineDescriptor = forEngine(testEngine).with(ContainerClass.class).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(ContainerClass.class));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(ContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void executeEngineWithClassWithTests() throws NoSuchMethodException {
		TestDescriptor engineDescriptor = forEngine(testEngine).with(
				forClass(ContainerClass.class, "succeeding", "failing")
		).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(ContainerClass.class));
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "failing"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "failing"), isFailed("expected fail"));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(ContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private void executeTests(TestDescriptor engineDescriptor) {
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder, null);
		new JqwikExecutor().execute(executionRequest, engineDescriptor);
	}

	private static class ContainerClass {
		@Example
		public void succeeding() {
		}

		@Example
		public void failing() {
			fail("expected fail");
		}
	}
}
