package net.jqwik.engine.execution.lifecycle;

import java.util.*;

/**
 * Wraps the instance of a test container class and potentially its outer classes.
 */
public class TestInstances {

	private final List<Object> testInstances;

	public TestInstances(Object testInstance) {
		this.testInstances = Collections.singletonList(testInstance);
	}

	public TestInstances(Object testInstance, TestInstances outerInstances) {
		this.testInstances = new ArrayList<>(outerInstances.testInstances);
		this.testInstances.add(0, testInstance);
	}

	private TestInstances(List<Object> testInstances) {
		this.testInstances = testInstances;
	}

	public Object target() {
		if (testInstances.size() > 0) {
			return testInstances.get(0);
		} else {
			throw new IllegalStateException("No target instance available");
		}
	}

	public Optional<TestInstances> outer() {
		if (testInstances.size() > 1) {
			List<Object> rest = testInstances.subList(1, testInstances.size());
			return Optional.of(new TestInstances(rest));
		} else {
			return Optional.empty();
		}
	}

	public List<Object> all() {
		return testInstances;
	}
}
