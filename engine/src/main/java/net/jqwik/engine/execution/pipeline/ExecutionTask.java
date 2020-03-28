package net.jqwik.engine.execution.pipeline;

import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

public interface ExecutionTask {

	UniqueId ownerId();

	TaskExecutionResult execute(PropertyExecutionListener listener, TaskExecutionResult predecessorResult);

	static ExecutionTask from(
		BiFunction<PropertyExecutionListener, TaskExecutionResult, TaskExecutionResult> executor,
		TestDescriptor owner,
		String description
	) {
		return new ExecutionTask() {
			@Override
			public UniqueId ownerId() {
				return owner.getUniqueId();
			}

			@Override
			public TaskExecutionResult execute(PropertyExecutionListener listener, TaskExecutionResult predecessorResult) {
				try {
					return CurrentTestDescriptor.runWithDescriptor(owner, () -> executor.apply(listener, predecessorResult));
				} catch (Throwable throwable) {
					JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
					return TaskExecutionResult.failure(throwable);
				}
			}

			@Override
			public String toString() {
				return "ExecutionTask: " + description;
			}
		};
	}

}
