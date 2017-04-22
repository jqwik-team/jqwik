package net.jqwik.execution;

import java.util.*;
import java.util.concurrent.*;

import org.junit.platform.engine.*;

public class ExecutionPipeline {

	interface ExecutionTask {
		UniqueId uniqueId();
		Set<ExecutionTask> predecessors();
		void execute(EngineExecutionListener listener);
	}

	private final EngineExecutionListener executionListener;

	private final CountDownLatch running = new CountDownLatch(1);
	private final CountDownLatch terminated = new CountDownLatch(1);

	public ExecutionPipeline(EngineExecutionListener executionListener) {
		this.executionListener = executionListener;
	}

	void submit(ExecutionTask task) {
	}

	void moveToFrontOfQueue(UniqueId uniqueId) {
	}

	void run() {
		running.countDown();
		terminated.countDown();
	}

	boolean shutdownAndWait(long timeout, TimeUnit unit) {
		try {
			return terminated.await(timeout, unit);
		} catch (InterruptedException e) {
			return false;
		}
	}

}
