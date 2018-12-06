package net.jqwik.engine.execution;

import org.junit.platform.engine.*;

import net.jqwik.engine.execution.pipeline.*;

@FunctionalInterface
public interface ExecutionTaskCreator {
	ExecutionTask createTask(TestDescriptor descriptor, Pipeline pipeline);
}
