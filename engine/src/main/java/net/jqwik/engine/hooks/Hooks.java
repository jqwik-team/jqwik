package net.jqwik.engine.hooks;

/**
 * The values here should give an overview about how build-in hooks are intertwined
 */
public class Hooks {

	public static class AroundProperty {

		// AutoCloseable.close() should usually be the last thing in the hook chain
		public static final int AUTO_CLOSEABLE_PROXIMITY = -100;

		// Should run shortly before AutoCloseableHook
		public static final int STATIC_PROPERTY_LIFECYCLE_PROXIMITY = -90;

		// Should run inside property lifecycle
		public static final int STATISTICS_PROXIMITY = -80;

		// Should run inside StatisticsHook
		public static final int EXPECT_FAILURE_PROXIMITY = -95;

	}

	public static class SkipExecution {

		// `@Disable` annotation
		public static final int DISABLED_ORDER = 0;

	}
}
