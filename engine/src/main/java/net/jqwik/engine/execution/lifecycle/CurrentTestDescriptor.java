package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class CurrentTestDescriptor {

	// Current test descriptors are stored in a stack because one test might invoke others
	// e.g. in JqwikIntegrationTests
	private static ThreadLocal<List<TestDescriptor>> descriptors = ThreadLocal.withInitial(ArrayList::new);

	public static void runWithDescriptor(TestDescriptor currentDescriptor, Runnable executable) {
		Supplier<Void> supplier = () -> {
			executable.run();
			return null;
		};
		runWithDescriptor(currentDescriptor, supplier);
	}

	public static <T> T runWithDescriptor(TestDescriptor currentDescriptor, Supplier<T> executable) {
		descriptors.get().add(0, currentDescriptor);
		try {
			return executable.get();
		} finally {
			TestDescriptor peek = descriptors.get().get(0);
			if (peek == currentDescriptor) {
				descriptors.get().remove(0);
			}
		}
	}

	public static TestDescriptor get() {
		if (descriptors.get().isEmpty()) {
			String message = String.format("The current action must be run on a jqwik thread, i.e. container, property or hook.%n" +
											   "Maybe you spawned off a thread?");
			throw new JqwikException(message);
		}
		return descriptors.get().get(0);
	}

}
