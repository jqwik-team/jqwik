package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NSingleValueShrinker<T> {
	private final NShrinkable<T> shrinkable;

	public NSingleValueShrinker(NShrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	//TODO: Return ShrinkResult to also transport assertion error
	public NShrinkable<T> shrink(Predicate<T> falsifier) {
		Set<NShrinkable<T>> allFalsified = collectAllFalsified(shrinkable.shrink(), new HashSet<>(), falsifier);
		return allFalsified.stream() //
			.sorted(Comparator.comparingInt(NShrinkable::distance)) //
			.findFirst().orElse(shrinkable);
	}

	private Set<NShrinkable<T>> collectAllFalsified(Set<NShrinkable<T>> toTry, Set<NShrinkable<T>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty()) return allFalsified;
		Set<NShrinkable<T>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkable -> {
			if (shrinkable.falsifies(falsifier)) {
				allFalsified.add(shrinkable);
				toTryNext.addAll(shrinkable.shrink());
			}
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}
}
