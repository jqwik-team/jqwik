package net.jqwik.execution.pipeline;

import java.util.function.*;

import org.junit.platform.engine.*;

@FunctionalInterface
public interface ExecutionTask {

	void execute(EngineExecutionListener listener);

	static ExecutionTask from(Consumer<EngineExecutionListener> consumer, String description) {
		return new ExecutionTask() {
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

	static ExecutionTask doNothing() {
		return from(listener -> {
		}, "doNothing");
	}
}
