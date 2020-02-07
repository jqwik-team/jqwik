package net.jqwik.engine.execution.pipeline;

import java.util.*;

public interface TaskExecutionResult {

	static TaskExecutionResult success() {
		return new TaskExecutionResult() {

			@Override
			public boolean successful() {
				return true;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}

			@Override
			public String toString() {
				return "TaskExecutionResult(successful)";
			}
		};
	}

	static TaskExecutionResult failure(Throwable throwable) {
		return new TaskExecutionResult() {

			@Override
			public boolean successful() {
				return false;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

			@Override
			public String toString() {
				return String.format("TaskExecutionResult(failure): %s", throwable.getMessage());
			}
		};
	}

	boolean successful();

	Optional<Throwable> throwable();

}
