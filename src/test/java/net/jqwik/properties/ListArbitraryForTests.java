package net.jqwik.properties;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ListArbitraryForTests implements NArbitrary<List<Integer>> {
	private final int size;

	public ListArbitraryForTests(int maxSize) {
		this.size = maxSize;
	}

	@Override
	public NShrinkableGenerator<List<Integer>> generator(int tries) {
		AtomicInteger index = new AtomicInteger(0);
		return new NShrinkableGenerator<List<Integer>>() {
			@Override
			public NShrinkable<List<Integer>> next(Random random) {
				if (index.get() <= size) {
					List<Integer> value = new ArrayList<>();
					for (int i = 1; i <= index.get(); i++) {
						value.add(i);
					}
					index.incrementAndGet();
					NShrinkCandidates<List<Integer>> shrinker = new ListShrinker();
					return new NShrinkableValue<>(value, shrinker);
				} else {
					index.set(0);
					return next(random);
				}
			}
		};
	}

	private static class ListShrinker implements NShrinkCandidates<List<Integer>> {

		@Override
		public Set<List<Integer>> nextCandidates(List<Integer> toShrink) {
			if (toShrink.isEmpty())
				return Collections.emptySet();
			if (toShrink.size() == 1) {
				List<Integer> shrunk = Collections.emptyList();
				return Collections.singleton(shrunk);
			}
			Set<List<Integer>> shrinkables = new HashSet<>();
			ArrayList<Integer> rightCut = new ArrayList<>(toShrink);
			rightCut.remove(rightCut.size() - 1);
			shrinkables.add(rightCut);
			ArrayList<Integer> leftCut = new ArrayList<>(toShrink);
			leftCut.remove(0);
			shrinkables.add(leftCut);
			return shrinkables;
		}

		@Override
		public int distance(List<Integer> value) {
			return value.size();
		}
	}
}
