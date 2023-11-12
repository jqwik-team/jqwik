package net.jqwik.engine.execution.lifecycle;

import java.util.*;

/**
 * Wraps the instance of a test container class and potentially its outer classes.
 */
public class ContainerInstances {

	private final List<Object> instances;

	public ContainerInstances(Object testInstance) {
		this.instances = Collections.singletonList(testInstance);
	}

	public ContainerInstances(Object testInstance, ContainerInstances outerInstances) {
		this.instances = new ArrayList<>(outerInstances.instances);
		this.instances.add(testInstance);
	}

	public Object target() {
		if (instances.isEmpty()) {
			throw new IllegalStateException("No target instance available");
		}
		return instances.get(instances.size() - 1);
	}

	/**
	 * Returns all instances, from outermost to innermost.
	 */
	public List<Object> all() {
		return instances;
	}
}
