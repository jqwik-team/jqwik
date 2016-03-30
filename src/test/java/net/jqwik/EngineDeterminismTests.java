
package net.jqwik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.pholser.junit.quickcheck.Property;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;

class EngineDeterminismTests extends AbstractEngineTests {

	private static List<List<Object>> parameterCalls = new ArrayList<>();

	@BeforeEach
	void initialize() {
		parameterCalls.clear();
	}

	@Test
	void executingSameDescriptorsTwiceGeneratesSameParameters() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
				ClassSelector.forClass(MyProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);
		Assertions.assertEquals(2, engineListener.countPropertiesSuccessful(), "Successful");

		List<List<Object>> paramsFirstRun = new ArrayList<>(parameterCalls);
		parameterCalls.clear();
		executeEngine(engineDescriptor);
		Assertions.assertEquals(paramsFirstRun, parameterCalls);
	}

	@Test
	void discoveringSameRequestTwiceGeneratesSameParametersOnExecute() {
		EngineDiscoveryRequest discoveryRequest1 = TestDiscoveryRequestBuilder.request().select(
				ClassSelector.forClass(MyProperties.class)).build();
		TestDescriptor engineDescriptor1 = engine.discover(discoveryRequest1, UniqueId.forEngine(engine.getId()));

		executeEngine(engineDescriptor1);
		List<List<Object>> paramsFirstRun = new ArrayList<>(parameterCalls);

		parameterCalls.clear();

		EngineDiscoveryRequest discoveryRequest2 = TestDiscoveryRequestBuilder.request().select(
				ClassSelector.forClass(MyProperties.class)).build();
		TestDescriptor engineDescriptor2 = engine.discover(discoveryRequest1, UniqueId.forEngine(engine.getId()));

		executeEngine(engineDescriptor2);
		Assertions.assertEquals(paramsFirstRun, parameterCalls);
	}

	static class MyProperties {
		@Property(trials = 10)
		void oneNumber(int aNumber) {
			List<Object> aCall = Arrays.asList(aNumber);
			parameterCalls.add(aCall);
		}

		@Property(trials = 5)
		void succeedingProperty(int aNumber, String aString) {
			List<Object> aCall = Arrays.asList(aNumber, aString);
			parameterCalls.add(aCall);
		}

	}

}
