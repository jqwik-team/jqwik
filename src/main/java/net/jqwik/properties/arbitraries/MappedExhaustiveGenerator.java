package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class MappedExhaustiveGenerator<T, U> implements ExhaustiveGenerator<U> {
	private final ExhaustiveGenerator<T> toMap;
	private final Function<T, U> mapper;

	public MappedExhaustiveGenerator(ExhaustiveGenerator<T> toMap, Function<T, U> mapper) {this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public long maxCount() {
		return toMap.maxCount();
	}

	@Override
	public Iterator<U> iterator() {
		final Iterator<T> mappedIterator = toMap.iterator();
		return new Iterator<U>() {
			@Override
			public boolean hasNext() {
				return mappedIterator.hasNext();
			}

			@Override
			public U next() {
				return mapper.apply(mappedIterator.next());
			}
		};
	}
}
