package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.execution.pipeline.*;

import static org.assertj.core.api.Assertions.*;

class ExecutionPipelineTests {

	private final PropertyExecutionListener listener = Mockito.mock(PropertyExecutionListener.class);
	private final ExecutionPipeline pipeline = new ExecutionPipeline(listener);

	@Example
	void withNoTasksPipelineTerminatesAtOnce() {
		pipeline.runToTermination();
	}

	@Property(tries = 10)
	void tasksWithoutPredecessorsAreExecutedInOrderOfSubmission(@ForAll("taskList") @Size(max = 50) List<ExecutionTask> tasks) {
		tasks.forEach(t -> pipeline.submit(t));
		pipeline.runToTermination();
		InOrder events = Mockito.inOrder(listener);
		tasks.forEach(t -> events.verify(listener).executionStarted((MockExecutionTask) t));
	}

	@Property(tries = 10)
	void addingATaskTwiceThrowsException(@ForAll("task") ExecutionTask task) {
		pipeline.submit(task);
		assertThatThrownBy(() -> pipeline.submit(task)).isInstanceOf(DuplicateExecutionTaskException.class);
	}

	@Provide
	Arbitrary<List<ExecutionTask>> taskList() {
		return task().list();
	}

	@Provide
	Arbitrary<ExecutionTask> task() {
		return OrderedArbitraryForTesting.between(1, 100).map(i -> new MockExecutionTask(Integer.toString(i)));
	}

	@Example
	void tasksPutInFrontAreExecutedFirst() {
		MockExecutionTask task1 = new MockExecutionTask("1");
		MockExecutionTask task2 = new MockExecutionTask("2");
		MockExecutionTask task3 = new MockExecutionTask("3");
		pipeline.submit(task1);
		pipeline.submit(task2);
		pipeline.submit(task3);
		pipeline.executeFirst(task2, task3);
		pipeline.runToTermination();

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(task2);
		events.verify(listener).executionStarted(task3);
		events.verify(listener).executionStarted(task1);
	}

	@Example
	void predecessorsOfTasksAreExecutedFirst() {
		MockExecutionTask task1 = new MockExecutionTask("1");
		MockExecutionTask task2 = new MockExecutionTask("2");
		MockExecutionTask task3 = new MockExecutionTask("3");
		pipeline.submit(task1);
		pipeline.submit(task2, task1);
		pipeline.submit(task3, task2, task1);
		pipeline.executeFirst(task3, task2);
		pipeline.runToTermination();

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(task1);
		events.verify(listener).executionStarted(task2);
		events.verify(listener).executionStarted(task3);
	}

	@Example
	void predecessorsMustBeSubmittedBeforeATaskCanRun() {
		MockExecutionTask task1 = new MockExecutionTask("1");
		MockExecutionTask task2 = new MockExecutionTask("2");
		pipeline.submit(task1, task2);

		assertThatThrownBy(() -> pipeline.runToTermination()).isInstanceOf(PredecessorNotSubmittedException.class);
	}

	@Example
	void executeFirstByUniqueId() {
		MockExecutionTask task1 = new MockExecutionTask("1");
		MockExecutionTask task2 = new MockExecutionTask("2");
		pipeline.submit(task1, task2);
		pipeline.submit(task2);

		UniqueId ownerId = UniqueId.root("owner", "2");
		MockExecutionTask owned1 = new MockExecutionTask(ownerId, "1");
		MockExecutionTask owned2 = new MockExecutionTask(ownerId, "2");
		pipeline.submit(owned1);
		pipeline.submit(owned2, owned1);

		pipeline.executeFirst(ownerId);

		pipeline.runToTermination();

		InOrder events = Mockito.inOrder(listener);
		events.verify(listener).executionStarted(owned1);
		events.verify(listener).executionStarted(owned2);
		events.verify(listener).executionStarted(task2);
		events.verify(listener).executionStarted(task1);

	}

}
