package net.jqwik.execution;

import org.junit.platform.engine.*;

@FunctionalInterface
public interface TestDescriptorExecutor {
	void execute(TestDescriptor descriptor, EngineExecutionListener listener);
}
