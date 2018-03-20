package net.jqwik.api.stateful;

@FunctionalInterface
public interface Invariant<T> {

	void check(T model);
}
