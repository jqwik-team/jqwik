package net.jqwik.execution;

import org.junit.platform.engine.*;

public class ContainerExecutor {

	public void execute(TestDescriptor containerDescriptor, EngineExecutionListener listener, TestDescriptorExecutor childExecutor) {
		listener.executionStarted(containerDescriptor);
		TestExecutionResult result = new SafeExecutor().executeSafely(() -> {
			for (TestDescriptor descriptor : containerDescriptor.getChildren()) {
				childExecutor.execute(descriptor, listener);
			}
		});
		listener.executionFinished(containerDescriptor, result);

	}
}
