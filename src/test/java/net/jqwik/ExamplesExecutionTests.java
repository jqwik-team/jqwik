package net.jqwik;

import static net.jqwik.TestDescriptorBuilder.*;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.execution.AutoCloseableLifecycle;
import net.jqwik.execution.ExampleExecutor;
import org.junit.platform.engine.EngineExecutionListener;
import org.mockito.InOrder;
import org.mockito.Mockito;

import net.jqwik.api.Example;

class ExamplesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final ExampleExecutor executor = new ExampleExecutor();

	private static List<String> executions = new ArrayList<>();

	ExamplesExecutionTests() {
		executions.clear();
	}

	@Example
	void succeedingExample() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "succeeding").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		assertThat(executions).containsExactly("succeeding", "close");
	}

	@Example
	void failingExample() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "failing").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "failing"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "failing"), isFailed("expected fail"));
		assertThat(executions).containsExactly("failing", "close");
	}

	@Example
	void failingInClose() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "failingInClose").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "failingInClose"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "failingInClose"), isFailed("failing close"));
		assertThat(executions).containsExactly("failingInClose", "close");
	}

	@Example
	void failingTwiceInExampleAndInClose() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "failingTwice").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "failingTwice"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "failingTwice"), isFailed("expected fail"));
		assertThat(executions).containsExactly("failingTwice", "close");
	}

	private void executeTests(ExampleMethodDescriptor engineDescriptor) {
		executor.execute(engineDescriptor, eventRecorder, new AutoCloseableLifecycle());
	}

	private static class ContainerClass implements AutoCloseable {

		private boolean closeShouldFail = false;

		@Example
		public void succeeding() {
			executions.add("succeeding");
		}

		@Example
		public void failing() {
			executions.add("failing");
			fail("expected fail");
		}

		@Example
		public void failingInClose() {
			executions.add("failingInClose");
			closeShouldFail = true;
		}

		@Example
		public void failingTwice() {
			executions.add("failingTwice");
			closeShouldFail = true;
			fail("expected fail");
		}

		@Override
		public void close() {
			executions.add("close");
			if (closeShouldFail)
				throw new RuntimeException("failing close");
		}
	}
}
