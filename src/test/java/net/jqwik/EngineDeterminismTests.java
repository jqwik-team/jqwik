
package net.jqwik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;

import com.pholser.junit.quickcheck.Property;

class EngineDeterminismTests {

	private JqwikTestEngine engine;
	private static List<List<Object>> parameterCalls = new ArrayList<>();

	@BeforeEach
	void initialize() {
		parameterCalls.clear();
		engine = new JqwikTestEngine();
	}

	@Test
	void executingTestsTwiceWithSameEngineGeneratesSameParameters() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(MyProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		RecordingExecutionListener engineListener = executeEngine(engineDescriptor);
		Assertions.assertEquals(2, engineListener.countPropertiesSuccessful(), "Successful");

		System.out.println(parameterCalls);
		parameterCalls.clear();
		executeEngine(engineDescriptor);
		System.out.println(parameterCalls);


	}

	private RecordingExecutionListener executeEngine(TestDescriptor engineDescriptor) {
		RecordingExecutionListener engineListener = new RecordingExecutionListener();
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, engineListener);
		engine.execute(executionRequest);
		return engineListener;
	}

	static class MyProperties {
		@Property(trials = 10)
		void oneNumber(int aNumber) {
			List<Object> aCall = Arrays.asList(aNumber);
			parameterCalls.add(aCall);
		}

		@Property(trials = 5)
		void succeedingProperty(int aNumber, String aString) {

		}

	}

}
