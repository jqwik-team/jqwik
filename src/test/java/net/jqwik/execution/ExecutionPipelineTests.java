package net.jqwik.execution;

import net.jqwik.api.*;
import org.junit.platform.engine.*;
import org.mockito.*;

public class ExecutionPipelineTests {

	private EngineExecutionListener listener = Mockito.mock(EngineExecutionListener.class);
	private ExecutionPipeline pipeline = new ExecutionPipeline(listener);

	@Example
	boolean withNoTasksPipelineTerminatesAtOnce() {
		pipeline.run();
		return pipeline.awaitTermination();
	}
}
