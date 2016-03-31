package net.jqwik.api;

import org.opentest4j.TestAbortedException;

/**
 * Provide own assumptions in order to not depend on JUnit5 API
 */
public class Assumptions {
	public static void assume(boolean condition) {
		if (!condition)
			throw new TestAbortedException();
	}
}
