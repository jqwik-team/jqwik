package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.*;

public class NCombinedShrinkable<T> implements NShrinkable<T> {

	private final List<NShrinkable<Object>> shrinkables;
	private final Function<List<Object>, T> combineFunction;
	private final T value;

	public NCombinedShrinkable(List<NShrinkable<Object>> shrinkables, Function<List<Object>, T> combineFunction) {
		this.shrinkables = shrinkables;
		this.combineFunction = combineFunction;
		this.value = combine(shrinkables);
	}

	@Override
	public Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> shrinkResults = new HashSet<>();
		for (int i = 0; i < shrinkables.size(); i++) {
			Predicate<Object> shrinkableFalsifier = falsifierForPosition(falsifier, i);
			Set<NShrinkResult<NShrinkable<Object>>> singleShrinkableShrinkResults = shrinkables.get(i).shrinkNext(shrinkableFalsifier);
			shrinkResults.addAll(toSetOfCombinedShrinkables(singleShrinkableShrinkResults, i));
		}
		return shrinkResults;
	}

	private Set<NShrinkResult<NShrinkable<T>>> toSetOfCombinedShrinkables(Set<NShrinkResult<NShrinkable<Object>>> singleSet, int position) {
		return singleSet.stream() //
				.map(shrinkResult -> shrinkResult.map(shrunkValue -> {
					List<NShrinkable<Object>> newShrinkables = new ArrayList<>(shrinkables);
					newShrinkables.set(position, shrunkValue);
					return (NShrinkable<T>) new NCombinedShrinkable<>(newShrinkables, combineFunction);
				})) //
				.collect(Collectors.toSet());
	}

	private Predicate<Object> falsifierForPosition(Predicate<T> falsifier, int position) {
		return s -> {
			List<NShrinkable<Object>> newShrinkables = new ArrayList<>(shrinkables);
			newShrinkables.set(position, NShrinkable.unshrinkable(s));
			return falsifier.test(combine(newShrinkables));
		};
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int distance() {
		return shrinkables.stream().mapToInt(NShrinkable::distance).sum();
	}

	private T combine(List<NShrinkable<Object>> shrinkables) {
		List<Object> params = shrinkables.stream().map(NShrinkable::value).collect(Collectors.toList());
		return combineFunction.apply(params);
	}

	@Override
	public String toString() {
		return String.format("CombinedShrinkable[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof NShrinkable))
			return false;
		NShrinkable<?> that = (NShrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
