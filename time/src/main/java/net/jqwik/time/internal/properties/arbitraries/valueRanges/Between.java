package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public abstract class Between<T> {

	private T min;
	private T max;

	protected void checkValidity(T min, T max) {
		//Override if needed
		//do nothing in default case
	}

	protected boolean minIsBeforeMax(T min, T max) {
		if (min instanceof Comparable && max instanceof Comparable) {
			Comparable comparableMin = (Comparable) min;
			Comparable comparableMax = (Comparable) max;
			if (comparableMin.compareTo(comparableMax) > 0) {
				return false;
			}
		}
		return true;
	}

	public Between<T> set(T min, T max) {
		min = min != null ? min : this.min;
		max = max != null ? max : this.max;
		if (!minIsBeforeMax(min, max)) {
			T remember = min;
			min = max;
			max = remember;
		}
		checkValidity(min, max);
		this.min = min;
		this.max = max;
		return this;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

}
