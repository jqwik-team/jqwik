
package net.jqwik;

import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;

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

}
