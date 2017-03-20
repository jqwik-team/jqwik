package net.jqwik.execution;

import experiments.Group;
import net.jqwik.api.Example;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static net.jqwik.TestDescriptorBuilder.forClass;
import static net.jqwik.TestDescriptorBuilder.forMethod;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.anyString;

class ExamplesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final ExampleExecutor executor = new ExampleExecutor();

	private static List<String> executions = new ArrayList<>();

	ExamplesExecutionTests() {
		executions.clear();
	}

	@Example
	void succeeding() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "succeeding").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		assertThat(executions).containsExactly("succeeding", "close");
	}

	@Example
	void succeedingInInnerGroup() throws NoSuchMethodException {
		TestDescriptor classDescriptor = forClass(ContainerClass.class).with(
			forClass(ContainerClass.Inner.class, "innerSucceeding")
		).build();

		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) classDescriptor
				.getChildren().stream().findFirst().get().
						getChildren().stream().findFirst().get();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.Inner.class, "innerSucceeding"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.Inner.class, "innerSucceeding"), isSuccessful());
		assertThat(executions).containsExactly("inner succeeding", "inner close", "close");
	}

	@Example
	void failing() throws NoSuchMethodException {
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
	void failingTwiceInTargetAndInClose() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "failingTwice").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isExampleDescriptorFor(ContainerClass.class, "failingTwice"));
		events.verify(eventRecorder).executionFinished(isExampleDescriptorFor(ContainerClass.class, "failingTwice"), isFailed("expected fail"));
		assertThat(executions).containsExactly("failingTwice", "close");
	}

	@Example
	void methodWithParameterIsSkipped() throws NoSuchMethodException {
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) forMethod(ContainerClass.class, "withParameter", int.class).build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionSkipped(isExampleDescriptorFor(ContainerClass.class, "withParameter"), anyString());
		assertThat(executions).isEmpty();
	}


	private void executeTests(ExampleMethodDescriptor exampleMethodDescriptor) {
		executor.execute(exampleMethodDescriptor, eventRecorder, new AutoCloseableLifecycle());
	}

	private static class ContainerClass implements AutoCloseable {

		private boolean closeShouldFail = false;

		@Example
		public void succeeding() {
			executions.add("succeeding");
		}

		@Example
		public void withParameter(int aNumber) {
			executions.add("withParameter");
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

		@Group
		public class Inner implements AutoCloseable {

			@Example
			void innerSucceeding() {
				executions.add("inner succeeding");
			}

			@Override
			public void close() throws Exception {
				executions.add("inner close");
			}
		}
	}

}
