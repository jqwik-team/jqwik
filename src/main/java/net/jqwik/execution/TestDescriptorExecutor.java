package net.jqwik.execution;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;

@FunctionalInterface
public interface TestDescriptorExecutor {
	void execute(TestDescriptor descriptor, EngineExecutionListener listener);
}
