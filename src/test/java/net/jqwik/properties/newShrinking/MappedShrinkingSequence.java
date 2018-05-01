package net.jqwik.properties.newShrinking;

import java.util.function.*;

class MappedShrinkingSequence<T, U> implements ShrinkingSequence<U> {

	private final ShrinkingSequence<T> toMap;
	private final Function<T, U> mapper;

	MappedShrinkingSequence(ShrinkingSequence<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public boolean next(Runnable count, Consumer<U> reportFalsified) {
		return toMap.next(count, aT -> reportFalsified.accept(mapper.apply(aT)));
	}

	@Override
	public FalsificationResult<U> current() {
		return toMap.current().map(mapper);
	}
}
