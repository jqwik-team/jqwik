package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NMappedShrinkable<T, U> implements NShrinkable<U> {

	private final NShrinkable<T> toMap;
	private final Function<T, U> mapper;

	public NMappedShrinkable(NShrinkable<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public U value() {
		return mapper.apply(toMap.value());
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		return new MappedShrinkingSequence(falsifier);
	}

	@Override
	public ShrinkingDistance distance() {
		return toMap.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NMappedShrinkable<?, ?> that = (NMappedShrinkable<?, ?>) o;
		return toMap.equals(that.toMap);
	}

	@Override
	public int hashCode() {
		return toMap.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Mapped<%s>(%s)|%s", value().getClass().getSimpleName(), value(), toMap);
	}

	private class MappedShrinkingSequence implements ShrinkingSequence<U> {

		private final ShrinkingSequence<T> toMapSequence;

		private MappedShrinkingSequence(Falsifier<U> falsifier) {
			Falsifier<T> toMapFalsifier = aT -> falsifier.test(mapper.apply(aT));
			toMapSequence = toMap.shrink(toMapFalsifier);
		}

		@Override
		public boolean next(Runnable count, Consumer<U> reportFalsified) {
			Consumer<T> toMapReporter = aT -> reportFalsified.accept(mapper.apply(aT));
			return toMapSequence.next(count, toMapReporter);
		}

		@Override
		public FalsificationResult<U> current() {
			return toMapSequence.current().map(mapper);
		}
	}
}
