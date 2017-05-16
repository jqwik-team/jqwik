package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NCombinedShrinkable<T> implements NShrinkable<T> {

	private final List<NShrinkable<?>> shrinkables;
	private final Function<List<?>, T> combineFunction;
	private final T value;

	public NCombinedShrinkable(List<NShrinkable<?>> shrinkables, Function<List<?>, T> combineFunction) {
		this.shrinkables = shrinkables;
		this.combineFunction = combineFunction;
		this.value = combine(shrinkables);
	}

	@Override
	public Set<NShrinkable<T>> shrink() {
		Set<NShrinkable<T>> shrunkSet = new HashSet<>();
		for (int i = 0; i < shrinkables.size(); i++) {
			Set<? extends NShrinkable<?>> singleSet = shrinkables.get(i).shrink();
			for (NShrinkable<?> shrunk : singleSet) {
				List<NShrinkable<?>> newShrinkables = new ArrayList<>(shrinkables);
				newShrinkables.set(i, shrunk);
				shrunkSet.add(new NCombinedShrinkable<>(newShrinkables, combineFunction));
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

	private T combine(List<NShrinkable<?>> shrinkables) {
		List<?> params = shrinkables.stream().map(NShrinkable::value).collect(Collectors.toList());
		return combineFunction.apply(params);
	}

}
