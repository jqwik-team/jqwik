package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

public class WithSamplesGenerator<T> implements RandomGenerator<T> {
	private final AtomicInteger tryCount = new AtomicInteger(0);
	private final RandomGenerator<T> samplesGenerator;
	private final RandomGenerator<T> base;
	private final int numberOfSamples;

	public WithSamplesGenerator(RandomGenerator<T> base, T[] samples) {
		List<Shrinkable<T>> shrinkables = SampleShrinkable.listOf(samples);
		this.samplesGenerator = RandomGenerators.samplesFromShrinkables(shrinkables);
		this.numberOfSamples = shrinkables.size();
		this.base = base;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		if (tryCount.getAndIncrement() < numberOfSamples)
			return samplesGenerator.next(random);
		return base.next(random);
	}
}
