package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Arbitrary<U>> mapper;
	private final int tries;
	private final long randomSeed;
	private final Shrinkable<U> shrinkable;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> mapper, int tries, long randomSeed) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.tries = tries;
		this.randomSeed = randomSeed;
		this.shrinkable = generateShrinkable(toMap.value());
	}

	private Shrinkable<U> generateShrinkable(T value) {
		RandomGenerator<U> generator = mapper.apply(value).generator(tries);
		return generator.next(new Random(randomSeed));
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		return new FlatMappedShrinkingSequence(falsifier).andThen(aShrinkable -> {
			FlatMappedShrinkable<T, U> flatMappedShrinkable = (FlatMappedShrinkable<T, U>) aShrinkable;
			return flatMappedShrinkable.shrinkable.shrink(falsifier);
		});
	}

	@Override
	public U value() {
		return shrinkable.value();
	}

	@Override
	public ShrinkingDistance distance() {
		return toMap.distance().append(shrinkable.distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FlatMappedShrinkable)) return false;
		FlatMappedShrinkable<?, ?> that = (FlatMappedShrinkable<?, ?>) o;
		return Objects.equals(shrinkable.value(), that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkable.value());
	}

	@Override
	public String toString() {
		return String.format("FlatMapped<%s>(%s)|%s", value().getClass().getSimpleName(), value(), toMap);
	}

	//TODO: Use ShrinkingSequence.map instead. map() requires different function then.
	class FlatMappedShrinkingSequence implements ShrinkingSequence<U> {

		private final Falsifier<T> toMapFalsifier;
		private final ShrinkingSequence<T> toMapShrinkingSequence;

		public FlatMappedShrinkingSequence(Falsifier<U> falsifier) {
			toMapFalsifier = aT -> falsifier.test(generateShrinkable(aT).value());
			toMapShrinkingSequence = toMap.shrink(toMapFalsifier);
		}

		@Override
		public boolean next(Runnable count, Consumer<U> uReporter) {
			Consumer<T> tReporter = aT -> uReporter.accept(generateShrinkable(aT).value());
			return toMapShrinkingSequence.next(count, tReporter);
		}

		@Override
		public FalsificationResult<U> current() {
			FalsificationResult<T> currentT = toMapShrinkingSequence.current();
			FlatMappedShrinkable<T, U> shrinkableU = new FlatMappedShrinkable<>(currentT.shrinkable(), mapper, tries, randomSeed);
			return FalsificationResult.falsified(shrinkableU, currentT.throwable().orElse(null));
		}
	}
}
