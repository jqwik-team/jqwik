package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NMappedGenerator<T, U> implements NShrinkableGenerator<U> {

	private final NShrinkableGenerator<T> toMap;
	private final Function<T, U> mapper;

	public NMappedGenerator(NShrinkableGenerator<T> toMap, Function<T, U> mapper) {this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public NShrinkable<U> next(Random random) {
		NShrinkable<T> original = toMap.next(random);
		return new NMappedShrinkable<>(original, mapper);
	}
}
