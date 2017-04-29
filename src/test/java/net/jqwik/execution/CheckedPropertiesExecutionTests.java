package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.mockito.*;

import static net.jqwik.TestDescriptorBuilder.*;
import static net.jqwik.matchers.MockitoMatchers.*;
import static org.mockito.Mockito.*;

class CheckedPropertiesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final PropertyTaskCreator executor = new PropertyTaskCreator();
	private final ArgumentCaptor<ReportEntry> reportEntryCaptor = ArgumentCaptor.forClass(ReportEntry.class);

	private static int countTries = 0;

	@Example
	void failWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failWithANumber", int.class)
			.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"));
		events.verify(eventRecorder).reportingEntryPublished(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"),
			reportEntryCaptor.capture());
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"), isFailed());

		Assertions.assertThat(reportEntryCaptor.getValue().getKeyValuePairs()).containsKey("seed");
	}

	@Example
	void succeedWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedWithANumber", int.class)
			.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"));
		events.verify(eventRecorder).reportingEntryPublished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"),
			reportEntryCaptor.capture());
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"), isSuccessful());

		Assertions.assertThat(reportEntryCaptor.getValue().getKeyValuePairs()).containsKey("seed");
	}

	@Example
	void succeedWithThreeNumbers() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedWithThreeNumbers",
			int.class, int.class, int.class).build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedWithThreeNumbers"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithThreeNumbers"),
			isSuccessful());
	}

	@Example
	void triesParameterIsRespected() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedIn11Tries", int.class)
			.build();

		countTries = 0;
		executeTests(descriptor);
		verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedIn11Tries"), isSuccessful());
		Assertions.assertThat(countTries).isEqualTo(11);
	}

	private void executeTests(PropertyMethodDescriptor propertyMethodDescriptor) {
		MockPipeline pipeline = new MockPipeline();
		ExecutionTask task = executor.createTask(propertyMethodDescriptor, (testInstance) -> new AutoCloseableLifecycle());
		pipeline.submit(task);
		pipeline.runWith(eventRecorder);
	}

	private static class ContainerClass {

		@Property
		public boolean failWithANumber(@ForAll int anyNumber) {
			return anyNumber == 0;
		}

		@Property
		public boolean succeedWithANumber(@ForAll int aNumber) {
			return true;
		}

		@Property
		public boolean succeedWithThreeNumbers(@ForAll int n1, @ForAll int n2, @ForAll int n3) {
			return n1 + n2 + n3 == n3 + n2 + n1;
		}

		@Property(tries = 11)
		public boolean succeedIn11Tries(@ForAll int aNumber) {
			countTries++;
			return true;
		}
	}

}
