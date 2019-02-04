package net.jqwik.docs.contracts.eurocalc;

public interface Constraint<T> {

	boolean isValid(T value);
}
