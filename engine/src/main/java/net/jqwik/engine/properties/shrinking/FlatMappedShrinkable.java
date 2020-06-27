package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

public class FlatMappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, Shrinkable<U>> mapper;
	private final Supplier<Shrinkable<U>> shrinkableSupplier;

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Arbitrary<U>> toArbitraryMapper, int genSize, long randomSeed) {
		this(toMap, t -> toArbitraryMapper.apply(t).generator(genSize), randomSeed);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, RandomGenerator<U>> toGeneratorMapper, long randomSeed) {
		this(toMap, t -> toGeneratorMapper.apply(t).next(SourceOfRandomness.newRandom(randomSeed)));
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Function<T, Shrinkable<U>> mapper) {
		this(toMap, () -> mapper.apply(toMap.value()), mapper);
	}

	public FlatMappedShrinkable(Shrinkable<T> toMap, Supplier<Shrinkable<U>> shrinkableSupplier, Function<T, Shrinkable<U>> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.shrinkableSupplier = shrinkableSupplier;
	}

	private Shrinkable<U> generateShrinkable(T value) {
		return mapper.apply(value);
	}

	private Shrinkable<U> shrinkable() {
		return shrinkableSupplier.get();
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = falsifier.map(at -> generateShrinkable(at).value());
		return toMap.shrink(toMapFalsifier)
					.map(resultMapperToU(mapper))
					.andThen(aShrinkable -> {
						FlatMappedShrinkable<T, U> flatMappedShrinkable = (FlatMappedShrinkable<T, U>) aShrinkable;
						return flatMappedShrinkable.shrinkable().shrink(falsifier);
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
		suggestions.addAll(shrinkable().shrinkingSuggestions());
		for (Shrinkable<T> tShrinkable : toMap.shrinkingSuggestions()) {
			FlatMappedShrinkable<T, U> flatMappedShrinkable = new FlatMappedShrinkable<>(tShrinkable, mapper);
			suggestions.add(flatMappedShrinkable.shrinkable());
			suggestions.addAll(flatMappedShrinkable.shrinkingSuggestions());
		}
		suggestions.sort(null);
		return suggestions;
	}

	@Override
	public U value() {
		return shrinkable().value();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FlatMappedShrinkable<?, ?> that = (FlatMappedShrinkable<?, ?>) o;

		if (!toMap.equals(that.toMap)) return false;
		if (!mapper.equals(that.mapper)) return false;
		return shrinkableSupplier.equals(that.shrinkableSupplier);
	}

	@Override
	public int hashCode() {
		int result = toMap.hashCode();
		result = 31 * result + mapper.hashCode();
		result = 31 * result + shrinkableSupplier.hashCode();
		return result;
	}

	@Override
	public ShrinkingDistance distance() {
		return toMap.distance().append(shrinkable().distance());
	}

	@Override
	public String toString() {
		return String.format("FlatMapped<%s>(%s)|%s", value().getClass().getSimpleName(), value(), toMap);
	}

}
