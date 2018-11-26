package net.jqwik.execution.pipeline;

import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.execution.*;

public interface ExecutionTask {

	UniqueId ownerId();

	void execute(JqwikExecutionListener listener);

	static ExecutionTask from(Consumer<JqwikExecutionListener> consumer, UniqueId ownerId, String description) {
		return new ExecutionTask() {
			@Override
			public UniqueId ownerId() {
				return ownerId;
			}

			@Override
			public void execute(JqwikExecutionListener listener) {
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
