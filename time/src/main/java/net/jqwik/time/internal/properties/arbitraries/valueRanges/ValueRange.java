package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public abstract class ValueRange<T, U> {

	protected T min;
	protected T max;

	private void exceptionCheck() {
		//Override if needed
		//do nothing in default case
	}

	protected abstract void minMaxChanger();

	public void set(T min, T max) {
		exceptionCheck();
		this.min = min;
		this.max = max;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

}
