package net.jqwik.execution.pipeline;

import org.junit.platform.engine.*;

import java.util.function.*;

public interface ExecutionTask {

	UniqueId ownerId();

	void execute(EngineExecutionListener listener);

	static ExecutionTask from(Consumer<EngineExecutionListener> consumer, UniqueId ownerId, String description) {
		return new ExecutionTask() {
			@Override
			public UniqueId ownerId() {
				return ownerId;
			}

			@Override
			public void execute(EngineExecutionListener listener) {
				consumer.accept(listener);
			}

			@Override
			public String toString() {
				return "ExecutionTask: " + description;
			}
		};
	}

	static ExecutionTask doNothing(UniqueId ownerId) {
		return from(listener -> {
		}, ownerId, "doNothing");
	}
}
