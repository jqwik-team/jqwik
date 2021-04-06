package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public abstract class ValueRange<T> {

	private T min;
	private T max;

	protected void exceptionCheck(Parameter parameter) {
		//Override if needed
		//do nothing in default case
	}

	protected void minMaxChanger(Parameter parameter) {
		//Override if needed
		//do nothing in default case
	}

	public void set(T min, T max) {
		min = min != null ? min : this.min;
		max = max != null ? max : this.max;
		Parameter parameter = new Parameter(min, max);
		minMaxChanger(parameter);
		exceptionCheck(parameter);
		this.min = parameter.min;
		this.max = parameter.max;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	protected class Parameter {
		private T min;
		private T max;

		private Parameter(T min, T max) {
			this.min = min;
			this.max = max;
		}

		protected T getMin() {
			return min;
		}

		protected T getMax() {
			return max;
		}

		protected void changeMinMax() {
			T remember = min;
			min = max;
			max = remember;
		}
	}

}
