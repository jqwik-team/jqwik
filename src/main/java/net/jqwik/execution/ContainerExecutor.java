package net.jqwik.execution;

import org.junit.platform.engine.*;

import net.jqwik.execution.pipeline.*;
import net.jqwik.execution.pipeline.Pipeline.*;

public class ContainerExecutor {

	public void execute(TestDescriptor containerDescriptor, TestDescriptorExecutor childExecutor, Pipeline pipeline,
			ExecutionTask... predecessors) {
		pipeline.submit(listener -> listener.executionStarted(containerDescriptor), predecessors);
//		TestExecutionResult result = new SafeExecutor().executeSafely(() -> {
//			for (TestDescriptor descriptor : containerDescriptor.getChildren()) {
//				childExecutor.execute(descriptor, listener);
//			}
//		});
		TestExecutionResult result = TestExecutionResult.successful();
		pipeline.submit(listener -> listener.executionFinished(containerDescriptor, result));
	}
}
