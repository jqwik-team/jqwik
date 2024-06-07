package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class MappedExhaustiveGenerator<T extends @Nullable Object, U extends @Nullable Object> implements ExhaustiveGenerator<U> {
	private final ExhaustiveGenerator<T> toMap;
	private final Function<? super T, ? extends U> mapper;

	public MappedExhaustiveGenerator(ExhaustiveGenerator<T> toMap, Function<? super T, ? extends U> mapper) {this.toMap = toMap;
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
