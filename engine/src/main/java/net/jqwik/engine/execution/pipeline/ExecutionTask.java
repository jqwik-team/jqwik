package net.jqwik.engine.execution.pipeline;

import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;

public interface ExecutionTask {

	UniqueId ownerId();

	void execute(PropertyExecutionListener listener);

	static ExecutionTask from(Consumer<PropertyExecutionListener> consumer, TestDescriptor owner, String description) {
		return new ExecutionTask() {
			@Override
			public UniqueId ownerId() {
				return owner.getUniqueId();
			}

			@Override
			public void execute(PropertyExecutionListener listener) {
				CurrentTestDescriptor.runWithDescriptor(owner, () -> consumer.accept(listener));
			}

			@Override
			public String toString() {
				return "ExecutionTask: " + description;
			}
		};
	}

}
