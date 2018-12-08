package net.jqwik.engine.execution.pipeline;

import net.jqwik.api.*;

public class PredecessorNotSubmittedException extends JqwikException {

	public PredecessorNotSubmittedException(ExecutionTask task, ExecutionTask predecessor) {
		super(String.format("Predecessor [%s] must be submitted before [%s] can be run.", predecessor.toString(), task.toString()));
	}
}
