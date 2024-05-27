package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use this hook to determine if an annotated element should be skipped during
 * a test run or not. Evaluation of hooks is stopped
 * as soon as a single hook returns {@linkplain SkipResult#skip(String)}.
 */
@API(status = MAINTAINED, since = "1.4.0")
@FunctionalInterface
public interface SkipExecutionHook extends LifecycleHook {

	/**
	 * Determine if an annotated element should be skipped or not.
	 * In order to decide an implementor can use all information from {@code context}.
	 * Use {@linkplain SkipResult#doNotSkip()} or {@linkplain SkipResult#skip(String)} as return value.
	 *
	 * @param context An instance of {@linkplain ContainerLifecycleContext} or {@linkplain PropertyLifecycleContext}
	 *
	 * @return an instance of {@linkplain SkipResult}
	 */
	SkipResult shouldBeSkipped(LifecycleContext context);

	@API(status = INTERNAL)
	SkipExecutionHook DO_NOT_SKIP = descriptor -> SkipExecutionHook.SkipResult.doNotSkip();

	class SkipResult {

		/**
		 * Create instance of {@linkplain SkipResult} to make the current element being skipped.
		 *
		 * @param reason String to describe why the element will be skipped
		 * @return instance of {@linkplain SkipResult}
		 */
		public static SkipResult skip(@Nullable String reason) {
			return new SkipResult(true, reason);
		}

		/**
		 * Create instance of {@linkplain SkipResult} to make the current element not being skipped.
		 *
		 * @return instance of {@linkplain SkipResult}
		 */
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
		 * if available. Might not be present, especially when using {@linkplain #doNotSkip()}.
		 *
		 * @return instance of {@linkplain Optional}&lt;{@linkplain SkipResult}&gt;
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
