package net.jqwik.execution.pipeline;

import org.junit.platform.engine.*;

import java.util.*;
import java.util.stream.*;

public class ExecutionPipeline {

	public interface ExecutionTask {
		UniqueId uniqueId();

		Set<UniqueId> predecessors();

		void execute(EngineExecutionListener listener);
	}

	private final List<ExecutionTask> tasks = new ArrayList<>();
	private final Map<UniqueId, Boolean> taskFinished = new HashMap<>();
	private final EngineExecutionListener executionListener;

	public ExecutionPipeline(EngineExecutionListener executionListener) {
		this.executionListener = executionListener;
	}

	public void submit(ExecutionTask... tasks) {
		for (ExecutionTask task : tasks) {
			submitTask(task);
		}
	}

	private void submitTask(ExecutionTask task) {
		if (taskFinished.containsKey(task.uniqueId()))
			throw new DuplicateExecutionTaskException(task);
		else {
			assertNoCircularDependency(task.uniqueId(), task.predecessors());
			taskFinished.put(task.uniqueId(), false);
		}
		tasks.add(task);
	}

	private void assertNoCircularDependency(UniqueId uniqueId, Set<UniqueId> predecessors) {
		if (predecessors.isEmpty())
			return;
		if (predecessors.contains(uniqueId))
			throw new CircularTaskDependencyException(uniqueId);
		Set<UniqueId> predecessorsOfPredecessors = predecessors.stream() //
			.flatMap(this::predecessorIDs) //
			.collect(Collectors.toSet());
		assertNoCircularDependency(uniqueId, predecessorsOfPredecessors);
	}

	private Stream<? extends UniqueId> predecessorIDs(UniqueId predecessorId) {
		int predecessorIndex = indexOf(predecessorId);
		if (predecessorIndex < 0)
			return Stream.empty();
		ExecutionTask predecessor = tasks.get(predecessorIndex);
		return predecessor.predecessors().stream();
	}

	private int predecessorIndex(UniqueId predecessorId) {
		return indexOf(predecessorId);
	}

	public void executeFirst(UniqueId... uniqueIds) {
		executeFirst(Arrays.asList(uniqueIds));
	}

	public void executeFirst(List<UniqueId> uniqueIdList) {
		for (int i = uniqueIdList.size() - 1; i >= 0; i--) {
			moveToTopOfQueue(uniqueIdList.get(i));
		}
	}

	private void moveToTopOfQueue(UniqueId uniqueId) {
		int index = predecessorIndex(uniqueId);
		if (index > 0) {
			ExecutionTask task = tasks.remove(index);
			tasks.add(0, task);
		}
	}

	private int indexOf(UniqueId uniqueId) {
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).uniqueId().equals(uniqueId))
				return i;
		}
		return -1;
	}

	public void run() {
		while (!tasks.isEmpty()) {
			ExecutionTask head = tasks.get(0);
			if (movedPredecessorsToTopOfQueue(head))
				continue;
			head.execute(executionListener);
			taskFinished.put(head.uniqueId(), true);
			tasks.remove(0);
		}
	}

	private boolean movedPredecessorsToTopOfQueue(ExecutionTask head) {
		List<UniqueId> unfinishedPredecessors = head.predecessors() //
			.stream() //
			.filter(predecessorId -> !taskFinished.get(predecessorId)) //
			.collect(Collectors.toList());
		executeFirst(unfinishedPredecessors);
		return !unfinishedPredecessors.isEmpty();
	}

}
