package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import java.util.*;

import static net.jqwik.TestDescriptorBuilder.*;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class SimplePropertiesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final PropertyTaskCreator executor = new PropertyTaskCreator();

	private static List<String> executions = new ArrayList<>();

	SimplePropertiesExecutionTests() {
		executions.clear();
	}

	@Example
	void succeeding() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeeding").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		assertThat(executions).containsExactly("succeeding", "close");
	}

	@Example
	void succeedWithVoid() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedingWithVoid").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedingWithVoid"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedingWithVoid"), isSuccessful());
		assertThat(executions).containsExactly("succeedingWithVoid", "close");
	}


	@Example
	void succeedingWithBoxedBoolean() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "alsoSucceeding").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "alsoSucceeding"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "alsoSucceeding"), isSuccessful());
		assertThat(executions).containsExactly("alsoSucceeding", "close");
	}

	@Example
	void failing() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failing").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failing"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failing"), isFailed());
		assertThat(executions).containsExactly("failing", "close");
	}

	@Example
	void failingInClose() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failingInClose").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failingInClose"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failingInClose"), isFailed());
		assertThat(executions).containsExactly("failingInClose", "close");
	}

	@Example
	void failingTwiceInTargetAndInClose() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failingTwice").build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failingTwice"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failingTwice"), isFailed());
		assertThat(executions).containsExactly("failingTwice", "close");
	}

	@Example
	void methodWithUnboundParameterIsSkipped() {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "withParameter", int.class)
			.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionSkipped(isPropertyDescriptorFor(ContainerClass.class, "withParameter"), anyString());
		assertThat(executions).isEmpty();
	}

	private void executeTests(PropertyMethodDescriptor propertyMethodDescriptor) {
		MockPipeline pipeline = new MockPipeline();
		ExecutionTask task = executor.createTask(propertyMethodDescriptor, TestHelper.nullLifecycleSupplier());
		pipeline.submit(task);
		pipeline.runWith(eventRecorder);
	}

	private static class ContainerClass implements AutoCloseable {

		private boolean closeShouldFail = false;

		@Property(tries = 1)
		public boolean succeeding() {
			executions.add("succeeding");
			return true;
		}

		@Property(tries = 1)
		public void succeedingWithVoid() {
			executions.add("succeedingWithVoid");
		}

		@Property(tries = 1)
		public Boolean alsoSucceeding() {
			executions.add("alsoSucceeding");
			return Boolean.TRUE;
		}

		@Property(tries = 1)
		public String returnsString() {
			executions.add("returnsString");
			return "aString";
		}

		@Property(tries = 1)
		public boolean withParameter(int aNumber) {
			executions.add("withParameter");
			return true;
		}

		@Property(tries = 1)
		public boolean failing() {
			executions.add("failing");
			return false;
		}

		@Property(tries = 1)
		public boolean failingInClose() {
			executions.add("failingInClose");
			closeShouldFail = true;
			return true;
		}

		@Property(tries = 1)
		public boolean failingTwice() {
			executions.add("failingTwice");
			closeShouldFail = true;
			return false;
		}

		@Override
		public void close() {
			executions.add("close");
			if (closeShouldFail)
				throw new RuntimeException("failing close");
		}
	}

}
