package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.random.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Shrinkable<U>> mapper;

	public FlatMappedShrinkable(
		Shrinkable<T> toMap,
		Function<T, Arbitrary<U>> toArbitraryMapper,
		int genSize,
		JqwikRandomState randomSeed,
		boolean withEmbeddedEdgeCases
	) {
		this(toMap, t -> {
			Arbitrary<U> arbitrary = toArbitraryMapper.apply(t);
			return arbitrary.generator(genSize, withEmbeddedEdgeCases);
		}, randomSeed);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, RandomGenerator<U>> toGeneratorMapper, JqwikRandomState randomSeed) {
		this(toMap, t -> toGeneratorMapper.apply(t).next(SourceOfRandomness.newRandom(randomSeed)));
	}

	protected FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Shrinkable<U>> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	private Shrinkable<U> generateShrinkable(T value) {
		return mapper.apply(value);
	}

	@Override
	public Stream<Shrinkable<U>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkRightSide(),
			shrinkLeftSide(),
			shrinkLeftGrowRightSide()
		);
	}

	private Stream<Shrinkable<U>> shrinkRightSide() {
		final ShrinkingDistance rightDistance = shrinkable().distance();
		return shrinkable().shrink()
						   .filter(s -> s.distance().size() <= rightDistance.size())
						   .map(rightSide -> new FixedValueFlatMappedShrinkable<>(toMap, mapper, () -> rightSide));
	}

	private Stream<Shrinkable<U>> shrinkLeftSide() {
		// final ShrinkingDistance leftDistance = toMap.distance();
		return toMap.shrink()
					// Seems to make shrinking less effective in some cases:
					// .filter(s -> s.distance().size() <= leftDistance.size())
					.map(shrunkLeftSide -> new FlatMappedShrinkable<>(shrunkLeftSide, mapper));
	}

	private Stream<Shrinkable<U>> growRightSide() {
		return shrinkable().grow()
						   .map(rightSide -> new FixedValueFlatMappedShrinkable<>(toMap, mapper, () -> rightSide));
	}

	private Stream<Shrinkable<U>> shrinkLeftGrowRightSide() {
		return toMap.shrink()
					.map(shrunkLeftSide -> new FlatMappedShrinkable<>(shrunkLeftSide, mapper))
					.flatMap(FlatMappedShrinkable::growRightSide);
	}

	@Override
	public U value() {
		return shrinkable().value();
	}

	protected Shrinkable<U> shrinkable() {
		return generateShrinkable(toMap.value());
	}

	@Override
	public ShrinkingDistance distance() {
		return toMap.distance().append(shrinkable().distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FlatMappedShrinkable)) return false;
		FlatMappedShrinkable<?, ?> that = (FlatMappedShrinkable<?, ?>) o;
		return Objects.equals(value(), that.value());
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(value());
	}

	@Override
	public String toString() {
		return String.format(
			"%s<%s>(%s:%s)|%s",
			getClass().getSimpleName(),
			value().getClass().getSimpleName(),
			value(),
			distance(),
			toMap
		);
	}

}
