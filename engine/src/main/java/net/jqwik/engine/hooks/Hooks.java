package net.jqwik.engine.hooks;

/**
 * The values here should give an overview about how build-in hooks are intertwined
 */
public class Hooks {

	public static class AroundContainer {
		// Should run outside of standard hooks
		public static final int CONTAINER_LIFECYCLE_METHODS_PROXIMITY = -10;
	}

	public static class AroundProperty {
		// AutoCloseable.close() should usually be the last thing in the hook chain
		public static final int AUTO_CLOSEABLE_PROXIMITY = -100;

		// Should run outside of standard hooks
		public static final int STATISTICS_PROXIMITY = -50;

		// Should run close to property method
		public static final int PROPERTY_LIFECYCLE_METHODS_PROXIMITY = -10;
	}

	public static class AroundTry {
		// Should run close to property method
		public static final int TRY_LIFECYCLE_METHODS_PROXIMITY = -10;

		// Closer to property method than lifecycle methods
		public static final int TRY_RESOLVE_FOOTNOTES_PROXIMITY = -20;
	}

}
