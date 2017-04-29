package net.jqwik.execution;

import net.jqwik.execution.pipeline.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

public class MockExecutionTask extends AbstractTestDescriptor implements ExecutionTask {

	public MockExecutionTask(String name) {
		this(UniqueId.root("test", name), name);
	}

	public MockExecutionTask(UniqueId uniqueId, String name) {
		super(uniqueId.append("task", name), name);
	}

	@Override
	public UniqueId ownerId() {
		return getUniqueId();
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
