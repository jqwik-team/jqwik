package net.jqwik.execution;

import net.jqwik.execution.pipeline.ExecutionPipeline.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.util.*;

public class MockExecutionTask extends AbstractTestDescriptor implements ExecutionTask {

	private final Set<UniqueId> predecessors;

	public MockExecutionTask(UniqueId id, UniqueId... predecessors) {
		super(id, id.toString());
		this.predecessors = new HashSet<>(Arrays.asList(predecessors));;
	}

	@Override
	public UniqueId uniqueId() {
		return getUniqueId();
	}

	@Override
	public Set<UniqueId> predecessors() {
		return predecessors;
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
