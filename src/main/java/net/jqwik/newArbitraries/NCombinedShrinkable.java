package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
	public Set<NShrinkable<T>> shrink() {
		Set<NShrinkable<T>> shrunkSet = new HashSet<>();
		for (int i = 0; i < shrinkables.size(); i++) {
			Set<NShrinkable<Object>> singleSet = shrinkables.get(i).shrink();
			for (NShrinkable<Object> shrunk : singleSet) {
				List<NShrinkable<Object>> newShrinkables = new ArrayList<>(shrinkables);
				newShrinkables.set(i, shrunk);
				shrunkSet.add(new NCombinedShrinkable<T>(newShrinkables, combineFunction));
			}
		}
		return shrunkSet;
	}

	@Override
	public boolean falsifies(Predicate<T> falsifier) {
		return falsifier.negate().test(value);
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

}
