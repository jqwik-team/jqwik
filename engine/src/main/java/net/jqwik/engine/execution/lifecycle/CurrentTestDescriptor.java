package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.jspecify.annotations.*;
import org.junit.platform.engine.*;

public class CurrentTestDescriptor {

	// Current test descriptors are stored in a stack because one test might invoke others
	// e.g. in JqwikIntegrationTests
	private static final ThreadLocal<List<TestDescriptor>> descriptors = ThreadLocal.withInitial(ArrayList::new);

	public static <T extends @Nullable Object> T runWithDescriptor(TestDescriptor currentDescriptor, Supplier<? extends T> executable) {
		push(currentDescriptor);
		try {
			return executable.get();
		} finally {
			TestDescriptor peek = descriptors.get().get(0);
			if (peek == currentDescriptor) {
				pop();
			}
		}
	}

	public static TestDescriptor pop() {
		return descriptors.get().remove(0);
	}

	public static void push(TestDescriptor currentDescriptor) {
		descriptors.get().add(0, currentDescriptor);
	}

	public static boolean isEmpty() {
		return descriptors.get().isEmpty();
	}

	public static TestDescriptor get() {
		if (isEmpty()) {
			String message = String.format("The current action must be run on a jqwik thread, i.e. container, property or hook.%n" +
											   "Maybe you spawned off a thread?");
			throw new OutsideJqwikException(message);
		}
		return descriptors.get().get(0);
	}

}
