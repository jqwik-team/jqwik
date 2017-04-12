package net.jqwik.properties;

@FunctionalInterface
public interface Generator<T> {

	T next();
}
