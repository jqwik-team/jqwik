
package net.jqwik;

import java.lang.reflect.Method;
import com.pholser.junit.quickcheck.Property;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.discovery.MethodSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;
import org.opentest4j.AssertionFailedError;
import net.jqwik.api.Assumptions;

class JqwikTestEngineTests {

	private JqwikTestEngine engine;

	@BeforeEach
	void canCreateEngine() {
		engine = new JqwikTestEngine();
	}

	@Test
	void executeSingleProperty() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			MethodSelector.forMethod(NoParamsProperties.class, "succeedingProperty")).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		Assertions.assertEquals(2, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(1, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(1, engineListener.countPropertiesSuccessful(), "Successful");
	}

	@Test
	void propertiesWithoutParams() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(NoParamsProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		Assertions.assertEquals(8, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(6, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(3, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(2, engineListener.countPropertiesFailed(), "Failed");
		Assertions.assertEquals(1, engineListener.countPropertiesAborted(), "Aborted");
		Assertions.assertEquals(1, engineListener.countPropertiesSkipped(), "Skipped");
	}

	@Test
	void propertiesWithUnmodifiedParams() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(UnmodifiedParamsProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));
		Assertions.assertEquals(6, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(5, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(2, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(3, engineListener.countPropertiesFailed(), "Failed");
	}

	@Test
	void checkForPropertyVerificationFailure() throws NoSuchMethodException {
		Method failingMethod = UnmodifiedParamsProperties.class.getDeclaredMethod("shrinking", new Class[] { int.class });
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			MethodSelector.forMethod(UnmodifiedParamsProperties.class, failingMethod)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		RecordingExecutionListener.ExecutionEvent failingEvent = engineListener.filterEvents(
			event -> event.type == RecordingExecutionListener.ExecutionEventType.Failed).findFirst().get();

		Assertions.assertEquals("shrinking", failingEvent.descriptor.getName());
		PropertyVerificationFailure verificationFailure = (PropertyVerificationFailure) failingEvent.exception;
		Assertions.assertEquals(0, verificationFailure.getArgs()[0], "Shrinked value");
	}

	private RecordingExecutionListener executeEngine(TestDescriptor engineDescriptor) {
		RecordingExecutionListener engineListener = new RecordingExecutionListener();
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, engineListener);
		engine.execute(executionRequest);
		return engineListener;
	}

	static class NoParamsProperties {
		@Property
		String skipBecauseItDoesNotReturnBoolean() {
			return "a string";
		}

		@Property
		boolean succeedingProperty() {
			return true;
		}

		@Property
		boolean failingProperty() {
			return false;
		}

		@Property
		void succeedingVoid() {
		}

		@Property
		void failingVoid() {
			throw new AssertionFailedError("failing property");
		}

		@Property
		static void succeedingStatic() {

		}

		@Property
		void shouldBeSkipped() {
			Assumptions.assume(false);
		}
	}

	static class UnmodifiedParamsProperties {
		@Property
		boolean succeeding(int aNumber) {
			return true;
		}

		@Property
		boolean failingWithTwo(int first, int second) {
			return first > second;
		}

		@Property
		boolean succeedingWithTwo(int first, int second) {
			Assumptions.assume(first > second);
			return first > second;
		}

		@Property
		boolean shrinking(int aNumber) {
			return aNumber > 0;
		}

		@Property
		void failingWithVoid(int first, int second) {
			throw new AssertionFailedError();
		}
	}
}
