package net.jqwik.properties.shrinking;

import java.util.*;

public class ListShrinker<T> implements Shrinker<List<T>> {
	public <T> ListShrinker(Shrinker<T> elementShrinker) {
	}

	@Override
	public Shrinkable<List<T>> shrink(List<T> list) {
		ShrinkableList<T> tShrinkableList = new ShrinkableList<>();
		return tShrinkableList;
	}
}
