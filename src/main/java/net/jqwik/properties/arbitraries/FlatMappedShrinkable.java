package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FlatMappedShrinkable<T, U> implements NShrinkable<U> {

	private final NShrinkable<T> toMap;
	private final Function<T, Arbitrary<U>> mapper;
	private final int tries;
	private final long randomSeed;
	private final NShrinkable<U> shrinkable;

	public FlatMappedShrinkable(NShrinkable<T> toMap, Function<T, Arbitrary<U>> mapper, int tries, long randomSeed) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.tries = tries;
		this.randomSeed = randomSeed;
		this.shrinkable = generateShrinkable(toMap.value());
	}

	private NShrinkable<U> generateShrinkable(T value) {
		RandomGenerator<U> generator = mapper.apply(value).generator(tries);
		return generator.next(new Random(randomSeed));
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		return null;
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
		if (o == null || !(o instanceof FlatMappedShrinkable)) return false;
		FlatMappedShrinkable<?, ?> that = (FlatMappedShrinkable<?, ?>) o;
		return Objects.equals(shrinkable.value(), that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkable.value());
	}

	@Override
	public String toString() {
		return String.format("Mapped<%s>(%s)|%s", value().getClass().getSimpleName(), value(), toMap);
	}
}
