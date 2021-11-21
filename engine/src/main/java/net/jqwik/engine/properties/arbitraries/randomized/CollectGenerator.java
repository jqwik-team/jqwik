package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class CollectGenerator<T> implements RandomGenerator<List<T>> {
	private final RandomGenerator<T> elementGenerator;
	private final Predicate<List<T>> until;

	public CollectGenerator(RandomGenerator<T> elementGenerator, Predicate<List<T>> until) {
		this.elementGenerator = elementGenerator;
		this.until = until;
	}

	@Override
	public Shrinkable<List<T>> next(Random random) {
		List<T> base = new ArrayList<>();
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		MaxTriesLoop.loop(
			() -> !until.test(base),
			ignore -> {
				Shrinkable<T> shrinkable = elementGenerator.next(random);
				base.add(shrinkable.value());
				shrinkables.add(shrinkable);
				return Tuple.of(false, ignore);
			},
			maxMisses -> {
				String message =
					String.format("Generated list not fulfilled condition after maximum of %s elements", maxMisses);
				return new JqwikException(message);
			},
                10000);
		return new CollectShrinkable<>(shrinkables, until);
	}
}
