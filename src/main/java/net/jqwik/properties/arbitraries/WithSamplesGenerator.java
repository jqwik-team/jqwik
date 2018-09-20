package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class WithSamplesGenerator<T> implements RandomGenerator<T> {
	private final AtomicInteger tryCount = new AtomicInteger(0);
	private final RandomGenerator<T> samplesGenerator;
	private final RandomGenerator<T> base;
	private final int numberOfSamples;

	public WithSamplesGenerator(T[] samples, RandomGenerator<T> base) {
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

	@Override
	public void reset() {
		samplesGenerator.reset();
		base.reset();
	}
}
