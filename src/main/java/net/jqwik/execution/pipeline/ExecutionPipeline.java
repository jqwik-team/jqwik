package net.jqwik.execution.pipeline;

import org.junit.platform.engine.*;

import java.util.*;
import java.util.stream.*;

public class ExecutionPipeline {

	public interface ExecutionTask {
		void execute(EngineExecutionListener listener);
	}

	private final List<ExecutionTask> tasks = new ArrayList<>();
	private final Map<ExecutionTask, Boolean> taskFinished = new IdentityHashMap<>();
	private final Map<ExecutionTask, ExecutionTask[]> taskPredecessors = new IdentityHashMap<>();
	private final EngineExecutionListener executionListener;

	public ExecutionPipeline(EngineExecutionListener executionListener) {
		this.executionListener = executionListener;
	}

	public void submit(ExecutionTask task, ExecutionTask...predecessors) {
		if (taskFinished.containsKey(task))
			throw new DuplicateExecutionTaskException(task);
		else {
			ensurePredecessorsSubmitted(task, predecessors);
			taskFinished.put(task, false);
			taskPredecessors.put(task, predecessors);
		}
		tasks.add(task);
	}

	private void ensurePredecessorsSubmitted(ExecutionTask task, ExecutionTask[] predecessors) {
		for (ExecutionTask predecessor : predecessors) {
			if (!taskFinished.containsKey(predecessor))
				throw new PredecessorNotSubmittedException(task, predecessor);
		}
	}

	public void executeFirst(ExecutionTask... executionTasks) {
		executeFirst(Arrays.asList(executionTasks));
	}

	public void executeFirst(List<ExecutionTask> executionTaskList) {
		for (int i = executionTaskList.size() - 1; i >= 0; i--) {
			moveToTopOfQueue(executionTaskList.get(i));
		}
	}

	private void moveToTopOfQueue(ExecutionTask task) {
		if (tasks.contains(task)) {
			tasks.remove(task);
			tasks.add(0, task);
		}
	}

	public void waitForTermination() {
		while (!tasks.isEmpty()) {
			ExecutionTask head = tasks.get(0);
			if (movedPredecessorsToTopOfQueue(head))
				continue;
			head.execute(executionListener);
			taskFinished.put(head, true);
			tasks.remove(0);
		}
	}

	private boolean movedPredecessorsToTopOfQueue(ExecutionTask head) {
		List<ExecutionTask> unfinishedPredecessors = Arrays.stream(taskPredecessors.get(head)) //
			.filter(predecessor -> !taskFinished.get(predecessor)) //
			.collect(Collectors.toList());
		executeFirst(unfinishedPredecessors);
		return !unfinishedPredecessors.isEmpty();
	}

}
