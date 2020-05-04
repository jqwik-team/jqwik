package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Shrinkable<U>> mapper;
	private final Shrinkable<U> shrinkable;
	private final U value;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> toArbitraryMapper, int genSize, long randomSeed) {
		this(toMap, t -> toArbitraryMapper.apply(t).generator(genSize), randomSeed);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, RandomGenerator<U>> toGeneratorMapper, long randomSeed) {
		this(toMap, t -> toGeneratorMapper.apply(t).next(SourceOfRandomness.newRandom(randomSeed)));
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Shrinkable<U>> mapper) {
		this(toMap, mapper.apply(toMap.value()), mapper);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Shrinkable<U> initialShrinkable, Function<T, Shrinkable<U>> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.shrinkable = initialShrinkable;
		this.value = initialShrinkable.value();
	}

	private Shrinkable<U> generateShrinkable(T value) {
		return mapper.apply(value);
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = aT -> falsifier.execute(generateShrinkable(aT).value());
		return toMap.shrink(toMapFalsifier)
					.map(resultMapperToU(mapper))
					.andThen(aShrinkable -> {
						FlatMappedShrinkable<T, U> flatMappedShrinkable = (FlatMappedShrinkable<T, U>) aShrinkable;
						return flatMappedShrinkable.shrinkable.shrink(falsifier);
					});
	}

	private static <T, U> Function<FalsificationResult<T>, FalsificationResult<U>> resultMapperToU(Function<T, Shrinkable<U>> mapper) {
		return result -> result.map(shrinkableT -> {
			return new FlatMappedShrinkable<>(shrinkableT, mapper);
		});
	}

	@Override
	public List<Shrinkable<U>> shrinkingSuggestions() {
		List<Shrinkable<U>> suggestions = new ArrayList<>();
		suggestions.addAll(shrinkable.shrinkingSuggestions());
		for (Shrinkable<T> tShrinkable : toMap.shrinkingSuggestions()) {
			FlatMappedShrinkable<T, U> flatMappedShrinkable = new FlatMappedShrinkable<>(tShrinkable, mapper);
			suggestions.add(flatMappedShrinkable.shrinkable);
			suggestions.addAll(flatMappedShrinkable.shrinkingSuggestions());
		}
		suggestions.sort(null);
		return suggestions;
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
