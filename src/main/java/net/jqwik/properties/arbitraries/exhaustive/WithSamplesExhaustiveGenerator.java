package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;

public class WithSamplesExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final T[] samples;
	private final ExhaustiveGenerator<T> base;

	public WithSamplesExhaustiveGenerator(ExhaustiveGenerator<T> base, T[] samples) {
		this.samples = samples;
		this.base = base;
	}

	@Override
	public boolean isUnique() {
		return base.isUnique();
	}

	@Override
	public long maxCount() {
		return base.maxCount() + samples.length;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private final AtomicInteger countDeliveredSamples = new AtomicInteger(0);
			Iterator<T> iterator = base.iterator();

			@Override
			public boolean hasNext() {
				if (samplesAvailable()) {
					return true;
				}
				return iterator.hasNext();
			}

			private boolean samplesAvailable() {
				return countDeliveredSamples.get() < samples.length;
			}

			@Override
			public T next() {
				if (samplesAvailable()) {
					return samples[countDeliveredSamples.getAndIncrement()];
				}
				return iterator.next();
			}
		};
	}
}
