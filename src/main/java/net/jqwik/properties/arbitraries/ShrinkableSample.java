package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

public class ShrinkableSample<T> extends AbstractShrinkable<T> {

	@SafeVarargs
	public static<T> List<NShrinkable<T>> of(T ... samples) {
		// TODO: Use a ShrinkingCandidates implementation instead
		List<NShrinkable<T>> shrinkables = new ArrayList<>();
		NShrinkable<T> shrinkable = null;
		for (int i = 0; i < samples.length; i++) {
			T sample = samples[i];
			if (i == 0)
				shrinkable = NShrinkable.unshrinkable(sample);
			else
				shrinkable = new ShrinkableSample<>(sample, shrinkable, i);
			shrinkables.add(shrinkable);
		}
		return shrinkables;
	}

	private final NShrinkable<T> smallerSample;
	private final int distance;

	public ShrinkableSample(T value, NShrinkable<T> smallerSample, int distance) {
		super(value);
		this.smallerSample = smallerSample;
		this.distance = distance;
	}

	@Override
	public Set<NShrinkable<T>> shrinkCandidatesFor(NShrinkable<T> shrinkable) {
		return Collections.singleton(smallerSample);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(distance);
	}

}
