package net.jqwik.properties.shrinking;

import net.jqwik.properties.*;

import java.util.*;

public class ListShrinker<T> implements Shrinker<List<T>> {
	public <T> ListShrinker(Arbitrary<T> elementArbitrary) {
	}

	@Override
	public Shrinkable<List<T>> shrink(List<T> list) {
		ShrinkableList<T> shrinkableList = new ShrinkableList<>();
		if (list.isEmpty())
			return shrinkableList;

		List<T> current = new ArrayList<>(list);
		while(!current.isEmpty()) {
			addShrinkStep(shrinkableList, current);
			current.remove(current.size() - 1);
		}
		addShrinkStep(shrinkableList, current);
		return shrinkableList;
	}

	private void addShrinkStep(ShrinkableList<T> shrinkableList, List<T> current) {
		shrinkableList.addStep(ShrinkableValue.of(new ArrayList<>(current), current.size()));
	}
}
