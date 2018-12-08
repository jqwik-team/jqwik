package net.jqwik.engine.execution.pipeline;

import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.execution.*;

public interface ExecutionTask {

	UniqueId ownerId();

	void execute(PropertyExecutionListener listener);

	static ExecutionTask from(Consumer<PropertyExecutionListener> consumer, UniqueId ownerId, String description) {
		return new ExecutionTask() {
			@Override
			public UniqueId ownerId() {
				return ownerId;
			}

			@Override
			public void execute(PropertyExecutionListener listener) {
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
