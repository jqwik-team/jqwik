package net.jqwik.engine.execution.lifecycle;

import java.util.*;

/**
 * Wraps the instance of a test container class and potentially its outer classes.
 */
public class TestInstances {

	private final List<Object> instances;

	public TestInstances(Object testInstance) {
		this.instances = Collections.singletonList(testInstance);
	}

	public TestInstances(Object testInstance, TestInstances outerInstances) {
		this.instances = new ArrayList<>(outerInstances.instances);
		this.instances.add(testInstance);
	}

	private TestInstances(List<Object> instances) {
		this.instances = instances;
	}

	public Object target() {
		if (instances.size() > 0) {
			return instances.get(instances.size() - 1);
		} else {
			throw new IllegalStateException("No target instance available");
		}
	}

	public Optional<TestInstances> outer() {
		if (instances.size() > 1) {
			List<Object> rest = instances.subList(1, instances.size());
			return Optional.of(new TestInstances(rest));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Returns all instances, from outermost to innermost.
	 */
	public List<Object> all() {
		return instances;
	}
}
