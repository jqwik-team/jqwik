package net.jqwik.execution.pipeline;

public interface Pipeline {

	void submit(ExecutionTask task, ExecutionTask... predecessors);
}
