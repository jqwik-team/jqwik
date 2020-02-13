package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(AutoCloseableLifecycleTests.CheckCloseCalls.class)
class AutoCloseableLifecycleTests implements AutoCloseable {

	public static List<String> calls = new ArrayList<>();

	AutoCloseableLifecycleTests() {
		calls.add("outer init");
	}

	@Override
	public void close() {
		calls.add("outer close");
	}

	@Property(tries = 5)
	void outer1(@ForAll int ignore) {
		calls.add("outer 1");
	}

	@Example
	void outer2() {
		calls.add("outer 2");
	}

	@Group
	class InnerCloseable implements AutoCloseable {

		public InnerCloseable() {
			calls.add("inner closeable init");
		}

		@Example
		void innerCloseable1() {
			calls.add("inner closeable 1");
		}

		@Example
		void innerCloseable2() {
			calls.add("inner closeable 2");
		}

		@Override
		public void close() throws Exception {
			calls.add("inner closeable close");
		}
	}

	@Group
	class InnerNotCloseable {
		public InnerNotCloseable() {
			calls.add("inner not closeable init");
		}

		@Example
		void innerNotCloseable1() {
			calls.add("inner not closeable 1");
		}
	}

	static class CheckCloseCalls implements AfterContainerHook {

		@Override
		public void afterContainer(ContainerLifecycleContext context) {
			Assertions.assertThat(calls).containsExactlyInAnyOrder(
				"outer init",
				"inner not closeable init",
				"inner not closeable 1",
				"outer close",
				"outer init",
				"inner closeable init",
				"inner closeable 1",
				"inner closeable close",
				"outer close",
				"outer init",
				"inner closeable init",
				"inner closeable 2",
				"inner closeable close",
				"outer close",
				"outer init",
				"outer 1",
				"outer 1",
				"outer 1",
				"outer 1",
				"outer 1",
				"outer close",
				"outer init",
				"outer 2",
				"outer close"
			);
		}
	}
}
