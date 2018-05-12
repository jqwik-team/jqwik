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
		Falsifier<T> toMapFalsifier = aT -> falsifier.test(generateShrinkable(aT).value());
		return toMap.shrink(toMapFalsifier).map(aT -> generateShrinkable(aT).value());
	}


//	@Override
//	public Set<ShrinkResult<NShrinkable<U>>> shrinkNext(Predicate<U> falsifier) {
//		Predicate<T> toMapPredicate = aT -> falsifier.test(generateShrinkable(aT).value());
//		Set<ShrinkResult<NShrinkable<U>>> shrinkToMapResults = shrinkToMap(toMapPredicate);
//		if (shrinkToMapResults.isEmpty())
//			return shrinkable.shrinkNext(falsifier);
//		return shrinkToMapResults;
//	}
//
//	private Set<ShrinkResult<NShrinkable<U>>> shrinkToMap(Predicate<T> toMapPredicate) {
//		return toMap.shrinkNext(toMapPredicate).stream() //
//			.map(shrinkResult -> shrinkResult //
//				.map(shrunkValue -> (NShrinkable<U>) new FlatMappedShrinkable(shrunkValue, mapper, tries, randomSeed))) //
//			.collect(Collectors.toSet());
//	}

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
