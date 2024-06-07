package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class SampleShrinkable<T extends @Nullable Object> extends AbstractValueShrinkable<T> {

	private final List<T> samples;
	private final int index;

	@SafeVarargs
	public static<T> List<Shrinkable<T>> listOf(T ... samples) {
		List<T> samplesList = Arrays.asList(samples);
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
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
	public Stream<Shrinkable<T>> shrink() {
		int sampleIndex = this.index;
		if (sampleIndex == 0)
			return Stream.empty();
		return Stream.of(new SampleShrinkable<>(samples, sampleIndex - 1));
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(index);
	}

}
