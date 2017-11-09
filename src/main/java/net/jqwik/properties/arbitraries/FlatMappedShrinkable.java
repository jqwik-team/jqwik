package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
	public Set<ShrinkResult<Shrinkable<U>>> shrinkNext(Predicate<U> falsifier) {
		Predicate<T> toMapPredicate = aT -> falsifier.test(generateShrinkable(aT).value());
		return toMap.shrinkNext(toMapPredicate).stream() //
			.map(shrinkResult -> shrinkResult //
				.map(shrunkValue -> (Shrinkable<U>) new FlatMappedShrinkable(shrunkValue, mapper, tries, randomSeed))) //
			.collect(Collectors.toSet());
	}

	@Override
	public U value() {
		return shrinkable.value();
	}

	@Override
	public int distance() {
		return toMap.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof Shrinkable)) return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(shrinkable.value(), that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkable.value());
	}

	@Override
	public String toString() {
		return String.format("FlatMappedShrinkable[%s:%d]", shrinkable.value(), distance());
	}
}
