package net.jqwik.properties.shrinking;

public interface Shrinker<T> {
	Shrinkable<T> shrink(T value);
}
