package net.jqwik.properties.shrinking;

public interface Shrinker<T> {
	ShrinkTree<T> shrink(T value);
}
