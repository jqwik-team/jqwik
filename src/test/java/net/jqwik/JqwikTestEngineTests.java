
package net.jqwik;

import com.pholser.junit.quickcheck.Property;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;
import org.opentest4j.AssertionFailedError;

class JqwikTestEngineTests {

	private JqwikTestEngine engine;

	@BeforeEach
	void canCreateEngine() {
		engine = new JqwikTestEngine();
	}

	@Test
	void propertiesWithoutParams() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(NoParamsProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		Assertions.assertEquals(7, engineDescriptor.allDescendants().size());

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);

		Assertions.assertEquals(5, engineListener.countPropertiesStarted(), "Started");
		Assertions.assertEquals(3, engineListener.countPropertiesSuccessful(), "Successful");
		Assertions.assertEquals(2, engineListener.countPropertiesFailed(), "Failed");
		Assertions.assertEquals(1, engineListener.countPropertiesSkipped(), "Skipped");
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
	}
}
