package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Arbitrary<U>> mapper;
	private final int genSize;
	private final long randomSeed;
	private final Shrinkable<U> shrinkable;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> mapper, int genSize, long randomSeed) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.genSize = genSize;
		this.randomSeed = randomSeed;
		this.shrinkable = generateShrinkable(toMap.value());
	}

	private Shrinkable<U> generateShrinkable(T value) {
		RandomGenerator<U> generator = mapper.apply(value).generator(genSize);
		return generator.next(new Random(randomSeed));
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = aT -> falsifier.test(generateShrinkable(aT).value());
		return toMap.shrink(toMapFalsifier) //
					.map(result -> result.map(shrinkableT -> new FlatMappedShrinkable<>(result.shrinkable(), mapper, genSize, randomSeed))) //
					.andThen(aShrinkable -> {
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

}
