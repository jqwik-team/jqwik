package net.jqwik.execution;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.execution.pipeline.*;

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
	public void execute(JqwikExecutionListener listener) {
		listener.executionStarted(this);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}
}
