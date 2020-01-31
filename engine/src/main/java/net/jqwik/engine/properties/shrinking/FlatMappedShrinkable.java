package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, RandomGenerator<U>> mapper;
	private final long randomSeed;
	private final Shrinkable<U> shrinkable;
	private final U value;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> mapper, int genSize, long randomSeed) {
		this(toMap, value -> mapper.apply(value).generator(genSize), randomSeed);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, RandomGenerator<U>> mapper, long randomSeed) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.randomSeed = randomSeed;
		this.shrinkable = generateShrinkable(toMap.value());
		this.value = shrinkable.value();
	}

	private Shrinkable<U> generateShrinkable(T value) {
		RandomGenerator<U> generator = mapper.apply(value);
		return generator.next(new Random(randomSeed));
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = aT -> falsifier.test(generateShrinkable(aT).value());
		return toMap.shrink(toMapFalsifier)
					.map(result -> result.map(shrinkableT -> new FlatMappedShrinkable<>(result.shrinkable(), mapper, randomSeed)))
					.andThen(aShrinkable -> {
						FlatMappedShrinkable<T, U> flatMappedShrinkable = (FlatMappedShrinkable<T, U>) aShrinkable;
						return flatMappedShrinkable.shrinkable.shrink(falsifier);
					});
	}

	@Override
	public U value() {
		return value;
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

}
