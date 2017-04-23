package net.jqwik.execution.pipeline;

import net.jqwik.*;
import net.jqwik.execution.pipeline.ExecutionPipeline.*;
import org.junit.platform.engine.*;

public class PredecessorNotSubmittedException extends JqwikException {

	public PredecessorNotSubmittedException(ExecutionTask task, ExecutionTask predecessor) {
		super(String.format("Predecessor [%s] must be submitted before []%s.", predecessor.toString(), task.toString()));
	}
}
