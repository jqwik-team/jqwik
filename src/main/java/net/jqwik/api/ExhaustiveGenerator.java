package net.jqwik.api;

public interface ExhaustiveGenerator<T> extends Iterable<T> {

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

}
