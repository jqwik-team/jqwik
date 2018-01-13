package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.Predicate;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class ShrinkableSample<T> implements Shrinkable<T> {

	public static<T> List<Shrinkable<T>> of(T ... samples) {
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		Shrinkable<T> shrinkable = null;
		for (int i = 0; i < samples.length; i++) {
			T sample = samples[i];
			if (i == 0)
				shrinkable = Shrinkable.unshrinkable(sample);
			else
				shrinkable = new ShrinkableSample<>(sample, shrinkable, i);
			shrinkables.add(shrinkable);
		}
		return shrinkables;
	}

	private final T value;
	private final Shrinkable<T> shrinkingCandidate;
	private final int distance;

	public ShrinkableSample(T value, Shrinkable<T> shrinkingCandidate, int distance) {
		this.value = value;
		this.shrinkingCandidate = shrinkingCandidate;
		this.distance = distance;
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		return SafeFalsifier.falsify(falsifier, shrinkingCandidate) //
				.map(Collections::singleton) //
				.orElse(Collections.emptySet());
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int distance() {
		return distance;
	}

	@Override
	public String toString() {
		return String.format("ShrinkableSample[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
