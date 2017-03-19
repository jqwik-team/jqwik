package net.jqwik.execution;

import net.jqwik.api.Example;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Property;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.execution.properties.PropertyExecutor;
import org.junit.platform.engine.EngineExecutionListener;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static net.jqwik.TestDescriptorBuilder.forMethod;
import static net.jqwik.matchers.MockitoMatchers.*;

class CheckedPropertiesExecutionTests {

	private final EngineExecutionListener eventRecorder = Mockito.mock(EngineExecutionListener.class);
	private final PropertyExecutor executor = new PropertyExecutor();

	@Example
	void failWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "failWithANumber", int.class)
				.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "failWithANumber"), isFailed());
	}

	@Example
	void succeedWithSingleNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedWithANumber", int.class)
				.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithANumber"), isSuccessful());
	}

	@Example
	void succeedWithThreeNumber() throws NoSuchMethodException {
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) forMethod(ContainerClass.class, "succeedWithThreeNumbers", int.class, int.class, int.class)
				.build();

		executeTests(descriptor);

		InOrder events = Mockito.inOrder(eventRecorder);
		events.verify(eventRecorder).executionStarted(isPropertyDescriptorFor(ContainerClass.class, "succeedWithThreeNumbers"));
		events.verify(eventRecorder).executionFinished(isPropertyDescriptorFor(ContainerClass.class, "succeedWithThreeNumbers"), isSuccessful());
	}

	private void executeTests(PropertyMethodDescriptor propertyMethodDescriptor) {
		executor.execute(propertyMethodDescriptor, eventRecorder, new AutoCloseableLifecycle());
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
	}

}
