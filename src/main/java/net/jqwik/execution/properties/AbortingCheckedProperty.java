package net.jqwik.execution.properties;

public class AbortingCheckedProperty implements CheckedProperty {

	private final Throwable toThrow;
	private final long randomSeed;

	AbortingCheckedProperty(Throwable toThrow, long randomSeed) {
		this.toThrow = toThrow;
		this.randomSeed = randomSeed;
	}

	@Override
	public PropertyExecutionResult check() {
		return PropertyExecutionResult.aborted(toThrow, randomSeed);
	}

	@Override
	public int getTries() {
		return 0;
	}
}
