package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.util.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface SkipExecutionHook extends LifecycleHook<SkipExecutionHook> {

	@Override
	default int compareTo(SkipExecutionHook other) {
		return Integer.compare(this.order(), other.order());
	}

	default int order() {
		return 0;
	}

	SkipResult shouldBeSkipped(LifecycleContext context);

	class SkipResult {

		public static SkipResult skip(String reason) {
			return new SkipResult(true, reason);
		}

		public static SkipResult doNotSkip() {
			return new SkipResult(false, null);
		}

		private boolean skipped = false;
		private String reason;

		private SkipResult(boolean skipped, String reason) {
			this.skipped = skipped;
			this.reason = reason;
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
