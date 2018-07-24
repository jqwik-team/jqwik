package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.lifecycle.*;

import static net.jqwik.matchers.MockitoMatchers.*;

public class JqwikExecutorTests {

	private Set<UniqueId> previouslyFailedTests = new HashSet<>();
	private JqwikExecutor executor = new JqwikExecutor(new LifecycleRegistry(), testRun -> {}, previouslyFailedTests);

	@Example
	void previouslyFailedTestsAreRunFirst() {
		TestDescriptor descriptor = TestDescriptorBuilder.forClass(TestContainer.class, "test1", "test2", "test3").build();
		EngineExecutionListener listener = Mockito.mock(EngineExecutionListener.class);

		previouslyFailedTests.add(testId(descriptor, "test2()"));
		previouslyFailedTests.add(testId(descriptor, "test3()"));

		executor.execute(descriptor, listener);

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(isPropertyDescriptorFor(TestContainer.class, "test3"));
		events.verify(listener).executionStarted(isPropertyDescriptorFor(TestContainer.class, "test2"));
		events.verify(listener).executionStarted(isPropertyDescriptorFor(TestContainer.class, "test1"));
	}

	private UniqueId testId(TestDescriptor descriptor, String value) {
		return descriptor.getUniqueId().append(JqwikUniqueIDs.PROPERTY_SEGMENT_TYPE, value);
	}

	private static class TestContainer {
		@Property void test1() {
		}

		@Property void test2() {
		}

		@Property void test3() {
		}
	}
}
