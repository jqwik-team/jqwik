package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

public class CollectGenerator<T> implements RandomGenerator<List<T>> {
	private final RandomGenerator<T> elementGenerator;
	private final Predicate<? super List<? extends T>> until;

	public CollectGenerator(RandomGenerator<T> elementGenerator, Predicate<? super List<? extends T>> until) {
		this.elementGenerator = elementGenerator;
		this.until = until;
	}

	@Override
	public Shrinkable<List<T>> next(Random random) {
		List<T> base = new ArrayList<>();
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			if (until.test(base)) {
				return new CollectShrinkable<>(shrinkables, until);
			}
			Shrinkable<T> shrinkable = elementGenerator.next(random);
			base.add(shrinkable.value());
			shrinkables.add(shrinkable);
		}
		String message = String.format("Generated list not fulfilled condition after maximum of %s elements", 10000);
		throw new JqwikException(message);
	}
}
