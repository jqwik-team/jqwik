package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import static net.jqwik.TestDescriptorBuilder.*;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.assertj.core.api.Assertions.*;

class ContainerExecutionTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);

	@Example
	void emptyEngine() {
		TestDescriptor engineDescriptor = forEngine(testEngine).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void engineWithEmptyClass() {
		TestDescriptor engineDescriptor = forEngine(testEngine).with(ContainerClass.class).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(ContainerClass.class));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(ContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void engineWithClassWithTests() throws NoSuchMethodException {
		TestDescriptor engineDescriptor = forEngine(testEngine).with(forClass(ContainerClass.class, "succeeding", "failing")).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(ContainerClass.class));
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failing"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failing"), isFailed("expected fail"));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(ContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void engineWithTwoClasses() throws NoSuchMethodException {
		TestDescriptor engineDescriptor = forEngine(testEngine)
			.with(forClass(ContainerClass.class, "succeeding", "failing"), forClass(SecondContainerClass.class, "succeeding")).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(ContainerClass.class));
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failing"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failing"), isFailed("expected fail"));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(ContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(SecondContainerClass.class));
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(SecondContainerClass.class, "succeeding"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(SecondContainerClass.class, "succeeding"), isSuccessful());
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(SecondContainerClass.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	@Example
	void engineWithNestedGroups() throws NoSuchMethodException {
		TestDescriptor engineDescriptor = forEngine(testEngine).with(forClass(TopLevelContainer.class, "topLevelSuccess").with(
			forClass(TopLevelContainer.InnerGroup.class, "innerGroupSuccess")
				.with(forClass(TopLevelContainer.InnerGroup.InnerInnerGroup.class, "innerInnerGroupSuccess")),
			forClass(TopLevelContainer.AnotherGroup.class))).build();

		executeTests(engineDescriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(engineDescriptor);
		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(TopLevelContainer.class));

		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(TopLevelContainer.InnerGroup.class));

		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(TopLevelContainer.InnerGroup.InnerInnerGroup.class));
		events.verify(eventRecorder)
			.executionStarted(isPropertyDescriptorFor(TopLevelContainer.InnerGroup.InnerInnerGroup.class, "innerInnerGroupSuccess"));
		events.verify(eventRecorder).executionFinished(
			isPropertyDescriptorFor(TopLevelContainer.InnerGroup.InnerInnerGroup.class, "innerInnerGroupSuccess"), isSuccessful());
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(TopLevelContainer.InnerGroup.InnerInnerGroup.class),
			isSuccessful());

		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(TopLevelContainer.InnerGroup.class, "innerGroupSuccess"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(TopLevelContainer.InnerGroup.class, "innerGroupSuccess"),
													   isSuccessful());

		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(TopLevelContainer.InnerGroup.class), isSuccessful());

		events.verify(eventRecorder).executionStarted(isClassDescriptorFor(TopLevelContainer.AnotherGroup.class));
		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(TopLevelContainer.AnotherGroup.class), isSuccessful());

		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(TopLevelContainer.class, "topLevelSuccess"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(TopLevelContainer.class, "topLevelSuccess"), isSuccessful());

		events.verify(eventRecorder).executionFinished(isClassDescriptorFor(TopLevelContainer.class), isSuccessful());
		events.verify(eventRecorder).executionFinished(engineDescriptor, TestExecutionResult.successful());
	}

	private void executeTests(TestDescriptor engineDescriptor) {
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, eventRecorder, null);
		new JqwikExecutor(new LifecycleRegistry(), TestRunRecorder.NULL, new TestRunData()).execute(executionRequest, engineDescriptor);
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

	private static class SecondContainerClass {

		@Example
		public void succeeding() {
		}

	}

	private static class TopLevelContainer {

		@Example
		void topLevelSuccess() {
		}

		@Group
		static class InnerGroup {

			@Example
			void innerGroupSuccess() {
			}

			@Group
			static class InnerInnerGroup {
				@Example
				void innerInnerGroupSuccess() {
				}
			}

		}

		@Group
		static class AnotherGroup {

		}

	}
}
