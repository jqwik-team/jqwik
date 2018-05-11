package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

import java.util.*;

public class SampleShrinkable<T> extends AbstractShrinkable<T> {

	private final List<T> samples;
	private final int index;

	@SafeVarargs
	public static<T> List<NShrinkable<T>> listOf(T ... samples) {
		List<T> samplesList = Arrays.asList(samples);
		List<NShrinkable<T>> shrinkables = new ArrayList<>();
		for (int i = 0; i < samples.length; i++) {
			shrinkables.add(new SampleShrinkable<>(samplesList, i));
		}
		return shrinkables;
	}

	private SampleShrinkable(List<T> samples, int index) {
		super(samples.get(index));
		this.samples = samples;
		this.index = index;
	}

	@Override
	public Set<NShrinkable<T>> shrinkCandidatesFor(NShrinkable<T> shrinkable) {
		int sampleIndex = ((SampleShrinkable<T>) shrinkable).index;
		if (sampleIndex == 0)
			return Collections.emptySet();
		return Collections.singleton(new SampleShrinkable<>(samples, sampleIndex - 1));
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(index);
	}

}
