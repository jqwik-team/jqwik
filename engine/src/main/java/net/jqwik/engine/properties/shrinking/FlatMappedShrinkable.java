package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Shrinkable<U>> mapper;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> toArbitraryMapper, int genSize, long randomSeed) {
		this(toMap, t -> toArbitraryMapper.apply(t).generator(genSize), randomSeed);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, RandomGenerator<U>> toGeneratorMapper, long randomSeed) {
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
			shrinkLeftSide()
		);
	}

	private Stream<Shrinkable<U>> shrinkRightSide() {
		final ShrinkingDistance rightDistance = shrinkable().distance();
		return shrinkable().shrink()
						   .filter(s -> s.distance().size() <= rightDistance.size())
						   .map(rightSide -> new FixedValueFlatMappedShrinkable<>(toMap, mapper, () -> rightSide));
	}

	private Stream<Shrinkable<U>> shrinkLeftSide() {
		final ShrinkingDistance leftDistance = toMap.distance();
		return toMap.shrink()
					.filter(s -> s.distance().size() <= leftDistance.size())
					.map(shrunkLeftSide -> new FlatMappedShrinkable<>(shrunkLeftSide, mapper));
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
		return Objects.hash(value());
	}

	@Override
	public String toString() {
		return String.format("FlatMapped<%s>(%s:%s)|%s", value().getClass().getSimpleName(), value(), distance(), toMap);
	}

}
