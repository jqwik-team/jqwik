package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
@FunctionalInterface
public interface SkipExecutionHook extends LifecycleHook {

	SkipResult shouldBeSkipped(LifecycleContext context);

	default int compareTo(SkipExecutionHook other) {
		return Integer.compare(this.skipExecutionOrder(), other.skipExecutionOrder());
	}

	SkipExecutionHook DO_NOT_SKIP = descriptor -> SkipExecutionHook.SkipResult.doNotSkip();

	/**
	 * Lower order value means earlier evaluation
	 */
	default int skipExecutionOrder() {
		return 0;
	}

	class SkipResult {

		public static SkipResult skip(String reason) {
			return new SkipResult(true, reason);
		}

		public static SkipResult doNotSkip() {
			return new SkipResult(false, null);
		}

		private final boolean skipped;
		private final String reason;

		private SkipResult(boolean skipped, String reason) {
			this.skipped = skipped;
			this.reason = reason == null || reason.isEmpty() ? null : reason;
		}

		/**
		 * Whether execution of the context should be skipped.
		 *
		 * @return {@code true} if the execution should be skipped
		 */
		public boolean isSkipped() {
			return this.skipped;
		}

		/**
		 * Get the reason that execution of the context should be skipped,
		 * if available.
		 */
		public Optional<String> reason() {
			return Optional.ofNullable(reason);
		}

		@Override
		public String toString() {
			String skipString = skipped ? "skip" : "do not skip";
			String reasonString = reason().map(reason -> ": " + reason).orElse("");
			return String.format("SkipResult(%s%s)", skipString, reasonString);
		}

	}
}
