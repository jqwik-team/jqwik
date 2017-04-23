package net.jqwik.execution.pipeline;

import net.jqwik.*;
import net.jqwik.execution.pipeline.ExecutionPipeline.*;

public class DuplicateExecutionTaskException extends JqwikException {
	public DuplicateExecutionTaskException(ExecutionTask task) {
		super(String.format("Task [%s] has already been submitted.", task.toString()));
	}
}
