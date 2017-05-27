package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.*;

public class CombinedShrinkable<T> implements Shrinkable<T> {

	private final List<Shrinkable<Object>> shrinkables;
	private final Function<List<Object>, T> combineFunction;
	private final T value;

	public CombinedShrinkable(List<Shrinkable<Object>> shrinkables, Function<List<Object>, T> combineFunction) {
		this.shrinkables = shrinkables;
		this.combineFunction = combineFunction;
		this.value = combine(shrinkables);
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<ShrinkResult<Shrinkable<T>>> shrinkResults = new HashSet<>();
		for (int i = 0; i < shrinkables.size(); i++) {
			Predicate<Object> shrinkableFalsifier = falsifierForPosition(falsifier, i);
			Set<ShrinkResult<Shrinkable<Object>>> singleShrinkableShrinkResults = shrinkables.get(i).shrinkNext(shrinkableFalsifier);
			shrinkResults.addAll(toSetOfCombinedShrinkables(singleShrinkableShrinkResults, i));
		}
		return shrinkResults;
	}

	private Set<ShrinkResult<Shrinkable<T>>> toSetOfCombinedShrinkables(Set<ShrinkResult<Shrinkable<Object>>> singleSet, int position) {
		return singleSet.stream() //
				.map(shrinkResult -> shrinkResult.map(shrunkValue -> {
					List<Shrinkable<Object>> newShrinkables = new ArrayList<>(shrinkables);
					newShrinkables.set(position, shrunkValue);
					return (Shrinkable<T>) new CombinedShrinkable<>(newShrinkables, combineFunction);
				})) //
				.collect(Collectors.toSet());
	}

	private Predicate<Object> falsifierForPosition(Predicate<T> falsifier, int position) {
		return s -> {
			List<Shrinkable<Object>> newShrinkables = new ArrayList<>(shrinkables);
			newShrinkables.set(position, Shrinkable.unshrinkable(s));
			return falsifier.test(combine(newShrinkables));
		};
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int distance() {
		return shrinkables.stream().mapToInt(Shrinkable::distance).sum();
	}

	private T combine(List<Shrinkable<Object>> shrinkables) {
		List<Object> params = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return combineFunction.apply(params);
	}

	@Override
	public String toString() {
		return String.format("CombinedShrinkable[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
