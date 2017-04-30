package net.jqwik.properties.shrinking;

public interface Shrinker<T> {
	ShrinkTree<Integer> shrink(T value);
}
