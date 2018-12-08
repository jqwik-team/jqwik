package net.jqwik.engine.execution;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.engine.execution.pipeline.*;

class MockExecutionTask extends AbstractTestDescriptor implements ExecutionTask {

	MockExecutionTask(String name) {
		this(UniqueId.root("test", name), name);
	}

	MockExecutionTask(UniqueId uniqueId, String name) {
		super(uniqueId.append("task", name), name);
	}

	@Override
	public UniqueId ownerId() {
		return getUniqueId();
	}

	@Override
	public void execute(PropertyExecutionListener listener) {
		listener.executionStarted(this);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}
}
