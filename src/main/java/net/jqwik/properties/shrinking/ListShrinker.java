package net.jqwik.properties.shrinking;

import java.util.*;

public class ListShrinker<T> implements Shrinker<List<T>> {
	public <T> ListShrinker(Shrinker<T> elementShrinker) {
	}

	@Override
	public ShrinkTree<List<T>> shrink(List<T> list) {
		return ShrinkTree.empty();
	}
}
