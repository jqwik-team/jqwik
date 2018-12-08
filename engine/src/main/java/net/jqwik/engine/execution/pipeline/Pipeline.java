package net.jqwik.engine.execution.pipeline;

public interface Pipeline {

	void submit(ExecutionTask task, ExecutionTask... predecessors);
}
