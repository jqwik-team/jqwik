package net.jqwik.execution;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.execution.ExecutionPipeline.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.api.*;

public class ExecutionPipelineTests {

	private EngineExecutionListener listener = Mockito.mock(EngineExecutionListener.class);
	private ExecutionPipeline pipeline = new ExecutionPipeline(listener);

	@Example
	boolean withNoTasksPipelineTerminatesAtOnce() {
		pipeline.run();
		return pipeline.shutdownAndWait(100, TimeUnit.MILLISECONDS);
	}

	@Example
	boolean aPipelineNotRunningWillTimeoutOnShutdownAndWait() {
		return !pipeline.shutdownAndWait(100, TimeUnit.MILLISECONDS);
	}

	@Property
	boolean tasksWithoutPredecessorsAreExecutedInOrderOfSubmission(@ForAll("tasksWithoutPredecessors") List<ExecutionTask> tasks) {
		return false;
	}
}
