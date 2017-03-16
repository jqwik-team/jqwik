package net.jqwik.execution;

import static net.jqwik.TestDescriptorBuilder.forMethod;
import static net.jqwik.matchers.MockitoMatchers.*;

import org.junit.platform.engine.EngineExecutionListener;
import org.mockito.InOrder;
import org.mockito.Mockito;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.descriptor.PropertyMethodDescriptor;

class CheckedPropertiesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final PropertyExecutor executor = new PropertyExecutor();

	@Example
	void failWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failWithANumber", int.class).build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"), isFailed());
	}

	@Example
	void succeedWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedWithANumber", int.class).build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"), isSuccessful());
	}


	private void executeTests(PropertyMethodDescriptor propertyMethodDescriptor) {
		executor.execute(propertyMethodDescriptor, eventRecorder, new AutoCloseableLifecycle());
	}

	private static class ContainerClass {

		@Property
		public boolean failWithANumber(@ForAll int aNumber) {
			return aNumber != 0;
		}

		@Property
		public boolean succeedWithANumber(@ForAll int aNumber) {
			return true;
		}
	}

}
