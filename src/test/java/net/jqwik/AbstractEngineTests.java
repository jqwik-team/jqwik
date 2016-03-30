
package net.jqwik;

import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;

abstract class AbstractEngineTests {

	protected JqwikTestEngine engine;

	@BeforeEach
	void initEngine() {
		engine = new JqwikTestEngine();
	}

	protected RecordingExecutionListener executeEngine(TestDescriptor engineDescriptor) {
		RecordingExecutionListener engineListener = new RecordingExecutionListener();
		ExecutionRequest executionRequest = new ExecutionRequest(engineDescriptor, engineListener);
		engine.execute(executionRequest);
		return engineListener;
	}

	static UniqueId uniqueIdForClass(Class<?> myPropertiesClass) {
		return UniqueId.forEngine("jqwik").append("jqwik-class", myPropertiesClass.getName());
	}

	static UniqueId uniqueIdForMethod(Class<?> myPropertiesClass, String methodName) {
		return uniqueIdForClass(myPropertiesClass).append("jqwik-method", methodName);
	}

	static UniqueId uniqueIdForMethodAndSeed(Class<?> myPropertiesClass, String methodName, String seed) {
		return uniqueIdForClass(myPropertiesClass).append("jqwik-method", methodName).append("jqwik-seed", seed);
	}



}
