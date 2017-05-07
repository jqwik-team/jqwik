package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.*;

public class ShrinkableList<T> implements Shrinkable<List<T>> {

	private final Shrinkable<List<T>> underlying;
	private final Arbitrary<T> elementArbitrary;

	public ShrinkableList(Shrinkable<List<T>> underlying, Arbitrary<T> elementArbitrary) {
		this.underlying = underlying;
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public Optional<ShrinkResult<List<T>>> shrink(Predicate<List<T>> falsifier) {
		return underlying().shrink(falsifier).map(shrinkResult -> shrinkElements(shrinkResult, falsifier));
	}

	public Shrinkable<List<T>> underlying() {
		return underlying;
	}

	// TODO: Extract duplication with FalsifiedShrinker.shrink
	private ShrinkResult<List<T>> shrinkElements(ShrinkResult<List<T>> shrinkResult, Predicate<List<T>> falsifier) {
		List<T> originalParams = shrinkResult.value();

		AssertionError[] lastFalsifiedError = { shrinkResult.error().orElse(null) };
		List<T> lastFalsifiedParams = new ArrayList<>(originalParams);
		for (int i = 0; i < originalParams.size(); i++) {
			shrinkPosition(i, lastFalsifiedParams, lastFalsifiedError, falsifier);
		}

		ShrinkableValue<List<T>> last = ShrinkableValue.of(lastFalsifiedParams, shrinkResult.distanceToTarget());
		return ShrinkResult.of(last, lastFalsifiedError[0]);
	}

	private void shrinkPosition(int position, List<T> lastFalsifiedParams, AssertionError[] lastFalsifiedError,
			Predicate<List<T>> falsifier) {
		T currentParam = lastFalsifiedParams.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, lastFalsifiedParams, falsifier);
		Shrinkable<T> shrinkTree = elementArbitrary.shrinkableFor(currentParam);
		Optional<ShrinkResult<T>> shrinkResults = shrinkTree.shrink(elementFalsifier);
		shrinkResults.ifPresent(shrinkResult -> {
			lastFalsifiedParams.set(position, shrinkResult.value());
			shrinkResult.error().ifPresent(ae -> lastFalsifiedError[0] = ae);
		});
	}

	private Predicate<T> createFalsifierForPosition(int position, List<T> lastFalsifiedParams, Predicate<List<T>> falsifier) {
		return param -> {
			List<T> effectiveParams = new ArrayList<>(lastFalsifiedParams);
			effectiveParams.set(position, param);
			return falsifier.test(effectiveParams);
		};
	}
}
