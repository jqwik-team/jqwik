package net.jqwik.execution;

import net.jqwik.execution.pipeline.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

public class MockExecutionTask extends AbstractTestDescriptor implements Pipeline.ExecutionTask {

	public MockExecutionTask(String name) {
		super(UniqueId.root("test", name), name);
	}

	@Override
	public void execute(EngineExecutionListener listener) {
		listener.executionStarted(this);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}
}
