package net.jqwik.execution.pipeline;

import net.jqwik.*;

public class DuplicateExecutionTaskException extends JqwikException {
	public DuplicateExecutionTaskException(ExecutionTask task) {
		super(String.format("Task [%s] has already been submitted.", task.toString()));
	}
}
