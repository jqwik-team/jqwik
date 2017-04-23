package net.jqwik.execution.pipeline;

import org.junit.platform.engine.*;

public interface Pipeline {

	@FunctionalInterface
	interface ExecutionTask {
		void execute(EngineExecutionListener listener);
	}

	void submit(Pipeline.ExecutionTask task, Pipeline.ExecutionTask... predecessors);
}
