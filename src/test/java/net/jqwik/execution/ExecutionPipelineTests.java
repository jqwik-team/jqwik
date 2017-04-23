package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.execution.pipeline.*;
import net.jqwik.execution.pipeline.ExecutionPipeline.*;
import net.jqwik.properties.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ExecutionPipelineTests {

	private EngineExecutionListener listener = Mockito.mock(EngineExecutionListener.class);
	private ExecutionPipeline pipeline = new ExecutionPipeline(listener);

	@Example
	void withNoTasksPipelineTerminatesAtOnce() {
		pipeline.run();
	}

	@Property(tries = 10)
	void tasksWithoutPredecessorsAreExecutedInOrderOfSubmission(@ForAll("taskWithoutPredecessors") List<ExecutionTask> tasks) {
		tasks.forEach(t -> pipeline.submit(t));
		pipeline.run();
		InOrder events = Mockito.inOrder(listener);
		tasks.forEach(t -> events.verify(listener).executionStarted((MockExecutionTask) t));
	}

	@Property(tries = 10)
	void addingATaskTwiceThrowsException(@ForAll("taskWithoutPredecessors") ExecutionTask task) {
		pipeline.submit(task);
		assertThatThrownBy(() -> pipeline.submit(task)).isInstanceOf(DuplicateExecutionTaskException.class);
	}

	@Generate
	Arbitrary<MockExecutionTask> taskWithoutPredecessors() {
		return new CountingArbitrary().map(i -> {
			UniqueId id = UniqueId.root("test", Integer.toString(i));
			return new MockExecutionTask(id);
		});
	}

	@Example
	void tasksPutInFrontAreExecutedFirst() {
		MockExecutionTask task1 = new MockExecutionTask(UniqueId.root("test", "1"));
		MockExecutionTask task2 = new MockExecutionTask(UniqueId.root("test", "2"));
		MockExecutionTask task3 = new MockExecutionTask(UniqueId.root("test", "3"));
		pipeline.submit(task1, task2, task3);
		pipeline.executeFirst(task2.uniqueId(), task3.uniqueId());
		pipeline.run();

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(task2);
		events.verify(listener).executionStarted(task3);
		events.verify(listener).executionStarted(task1);
	}

	@Example
	void predecessorsOfTasksAreExecutedFirst() {
		MockExecutionTask task1 = new MockExecutionTask(UniqueId.root("test", "1"));
		MockExecutionTask task2 = new MockExecutionTask(UniqueId.root("test", "2"), task1.uniqueId());
		MockExecutionTask task3 = new MockExecutionTask(UniqueId.root("test", "3"), task2.uniqueId());
		pipeline.submit(task3, task2, task1);
		pipeline.run();

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(task1);
		events.verify(listener).executionStarted(task2);
		events.verify(listener).executionStarted(task3);
	}

	@Example
	void circularDependencyInPredecessorsIsRecognized() {
		UniqueId id3 = UniqueId.root("test", "3");
		MockExecutionTask task1 = new MockExecutionTask(UniqueId.root("test", "1"), id3);
		MockExecutionTask task2 = new MockExecutionTask(UniqueId.root("test", "2"), task1.uniqueId());
		MockExecutionTask task3 = new MockExecutionTask(id3, task2.uniqueId());

		pipeline.submit(task3, task2);

		assertThatThrownBy(() -> pipeline.submit(task1)).isInstanceOf(CircularTaskDependencyException.class);

	}

}
