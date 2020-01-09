package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.junit.platform.engine.*;

public class CurrentTestDescriptor {

	// Current test descriptors are stored in a stack because one test might invoke others
	// e.g. in JqwikIntegrationTests
	private static ThreadLocal<List<TestDescriptor>> descriptor = ThreadLocal.withInitial(ArrayList::new);

	public static void runWithDescriptor(TestDescriptor currentDescriptor, Runnable executable) {
		descriptor.get().add(0, currentDescriptor);
		try {
			executable.run();
		} finally {
			TestDescriptor peek = descriptor.get().get(0);
			if (peek == currentDescriptor) {
				descriptor.get().remove(0);
			}
		}
	}

	public static TestDescriptor get() {
		return descriptor.get().get(0);
	}

}
