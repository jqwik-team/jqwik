package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.execution.pipeline.*;
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
		pipeline.runToTermination();
	}

	@Property(tries = 10)
	void tasksWithoutPredecessorsAreExecutedInOrderOfSubmission(@ForAll("task") List<ExecutionTask> tasks) {
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

	@Generate
	Arbitrary<MockExecutionTask> task() {
		return new CountingArbitrary().map(i -> new MockExecutionTask(Integer.toString(i)));
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
		pipeline.submit(task3, task2);
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

}
