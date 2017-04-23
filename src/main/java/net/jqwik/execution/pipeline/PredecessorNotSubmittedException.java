package net.jqwik.execution.pipeline;

import net.jqwik.*;

public class PredecessorNotSubmittedException extends JqwikException {

	public PredecessorNotSubmittedException(Pipeline.ExecutionTask task, Pipeline.ExecutionTask predecessor) {
		super(String.format("Predecessor [%s] must be submitted before []%s.", predecessor.toString(), task.toString()));
	}
}
