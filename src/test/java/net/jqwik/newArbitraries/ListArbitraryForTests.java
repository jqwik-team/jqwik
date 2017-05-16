package net.jqwik.newArbitraries;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ListArbitraryForTests implements NArbitrary<List<Integer>> {
	private final int size;

	public ListArbitraryForTests(int maxSize) {this.size = maxSize;}

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
					NShrinker<List<Integer>> shrinker = new ListShrinker(value);
					return new NShrinkableValue<>(value, Integer.MAX_VALUE, shrinker);
				} else {
					index.set(0);
					return next(random);
				}
			}
		};
	}

	private static class ListShrinker implements NShrinker<List<Integer>> {
		private final List<Integer> toShrink;

		private ListShrinker(List<Integer> toShrink) {
			this.toShrink = toShrink;
		}

		@Override
		public Set<NShrinkable<List<Integer>>> shrink() {
			if (toShrink.isEmpty())
				return Collections.emptySet();
			if (toShrink.size() == 1) {
				List<Integer> shrunk = Collections.emptyList();
				return Collections.singleton(new NShrinkableValue<>(shrunk, 0, new ListShrinker(shrunk)));
			}
			Set<NShrinkable<List<Integer>>> shrinkables = new HashSet<>();
			ArrayList<Integer> rightCut = new ArrayList<>(toShrink);
			rightCut.remove(rightCut.size() -1);
			shrinkables.add(new NShrinkableValue<>(rightCut, rightCut.size(), new ListShrinker(rightCut)));
			ArrayList<Integer> leftCut = new ArrayList<>(toShrink);
			leftCut.remove(0);
			shrinkables.add(new NShrinkableValue<>(leftCut, leftCut.size(), new ListShrinker(leftCut)));
			return shrinkables;
		}
	}
}
