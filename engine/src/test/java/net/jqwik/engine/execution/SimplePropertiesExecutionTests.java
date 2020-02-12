package net.jqwik.engine.execution;

import java.util.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
import net.jqwik.engine.hooks.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.TestDescriptorBuilder.*;
import static net.jqwik.engine.matchers.PropertyExecutionResultMatchers.*;
import static net.jqwik.engine.matchers.TestDescriptorMatchers.*;

class SimplePropertiesExecutionTests {

	private final PropertyExecutionListener eventRecorder = Mockito.mock(PropertyExecutionListener.class);
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
	void methodWithUnboundParameterFails() {
		PropertyMethodDescriptor descriptor =
			(PropertyMethodDescriptor) forMethod(ContainerClass.class, "withParameter", int.class).build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "withParameter"), isFailed());
		assertThat(executions).containsExactly("close");
	}

	private void executeTests(PropertyMethodDescriptor propertyMethodDescriptor) {
		MockPipeline pipeline = new MockPipeline();
		LifecycleHooksSupplier lifecycleSupplier = TestHelper.lifecycleSupplier(Arrays.asList(new AutoCloseableHook()));
		ExecutionTask task = executor.createTask(propertyMethodDescriptor, lifecycleSupplier, false);
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
