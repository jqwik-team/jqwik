package net.jqwik.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class ContainerGenerator<T, C> implements RandomGenerator<C> {
	private final RandomGenerator<T> elementGenerator;
	private final Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable;
	private final Function<Random, Integer> sizeGenerator;

	ContainerGenerator(
		RandomGenerator<T> elementGenerator,
		Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
		Function<Random, Integer> sizeGenerator
	) {
		this.elementGenerator = elementGenerator;
		this.createShrinkable = createShrinkable;
		this.sizeGenerator = sizeGenerator;
	}

	@Override
	public Shrinkable<C> next(Random random) {
		int listSize = sizeGenerator.apply(random);
		List<Shrinkable<T>> list = new ArrayList<>();
		while (list.size() < listSize) {
			list.add(elementGenerator.next(random));
		}
		return createShrinkable.apply(list);
	}

}
