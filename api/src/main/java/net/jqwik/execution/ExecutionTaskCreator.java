package net.jqwik.execution;

import org.junit.platform.engine.*;

import net.jqwik.execution.pipeline.*;

@FunctionalInterface
public interface ExecutionTaskCreator {
	ExecutionTask createTask(TestDescriptor descriptor, Pipeline pipeline);
}
