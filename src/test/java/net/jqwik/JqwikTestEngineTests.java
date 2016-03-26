
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

		RecordingExecutionListener engineListener = new RecordingExecutionListener();
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, engineListener);
		engine.execute(executionRequest);

		Assertions.assertEquals(2, engineListener.propertiesStarted);
		Assertions.assertEquals(1, engineListener.propertiesSuccessful);
		Assertions.assertEquals(1, engineListener.propertiesFailed);
		Assertions.assertEquals(1, engineListener.propertiesSkipped);
	}

	static class NoParamsProperties {
		@Property
		void skipBecauseItDoesNotReturnBooleas() {
		}

		@Property
		boolean succeedingProperty() {
			return true;
		}

		@Property
		boolean failingProperty() {
			return false;
		}
	}
}
