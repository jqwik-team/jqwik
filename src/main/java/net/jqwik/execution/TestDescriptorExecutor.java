package net.jqwik.execution;

import org.junit.platform.engine.*;

import net.jqwik.execution.pipeline.*;
import net.jqwik.execution.pipeline.Pipeline.*;

@FunctionalInterface
public interface TestDescriptorExecutor {
	void execute(TestDescriptor descriptor, Pipeline pipeline, ExecutionTask... predecessors);
}
