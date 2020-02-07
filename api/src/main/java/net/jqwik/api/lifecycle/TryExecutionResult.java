package net.jqwik.api.lifecycle;

import java.util.*;

public interface TryExecutionResult {

	static TryExecutionResult satisfied() {
		return satisfied(false);
	}

	static TryExecutionResult satisfied(boolean shouldPropertyFinishEarly) {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.SATISFIED;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return shouldPropertyFinishEarly;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s)", status().name());
			}
		};
	}

	static TryExecutionResult falsified(Throwable throwable) {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.FALSIFIED;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return false;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s): %s", status().name(), throwable().map(Throwable::getMessage).orElse("null"));
			}
		};
	}

	static TryExecutionResult invalid() {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.INVALID;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return false;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s)", status().name());
			}

		};
	}

	default boolean isSatisfied() {
		return this.status() == Status.SATISFIED;
	}

	enum Status {
		SATISFIED,
		FALSIFIED,
		INVALID
	}

	Status status();

	Optional<Throwable> throwable();

	boolean shouldPropertyFinishEarly();

}
