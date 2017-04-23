package net.jqwik.execution;

import net.jqwik.execution.pipeline.ExecutionPipeline.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.util.*;

public class MockExecutionTask extends AbstractTestDescriptor implements ExecutionTask {

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
