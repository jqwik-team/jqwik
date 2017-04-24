package net.jqwik.execution.pipeline;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

public class ExecutionPipeline implements Pipeline {

	private final List<ExecutionTask> tasks = new ArrayList<>();
	private final Map<ExecutionTask, Boolean> taskFinished = new IdentityHashMap<>();
	private final Map<ExecutionTask, ExecutionTask[]> taskPredecessors = new IdentityHashMap<>();
	private final EngineExecutionListener executionListener;

	public ExecutionPipeline(EngineExecutionListener executionListener) {
		this.executionListener = executionListener;
	}

	@Override
	public void submit(ExecutionTask task, ExecutionTask... predecessors) {
		if (taskFinished.containsKey(task))
			throw new DuplicateExecutionTaskException(task);
		taskFinished.putIfAbsent(task, false);
		taskPredecessors.put(task, predecessors);
		if (!taskFinished.get(task))
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

	public void runToTermination() {
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
		ExecutionTask[] predecessors = taskPredecessors.get(head);
		ensurePredecessorsSubmitted(head, predecessors);
		List<ExecutionTask> unfinishedPredecessors = Arrays.stream(predecessors) //
				.filter(predecessor -> !taskFinished.get(predecessor)) //
				.collect(Collectors.toList());
		executeFirst(unfinishedPredecessors);
		return !unfinishedPredecessors.isEmpty();
	}

}
