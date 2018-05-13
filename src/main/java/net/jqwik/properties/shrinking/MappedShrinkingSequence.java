package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class MappedShrinkingSequence<T, U> implements ShrinkingSequence<U> {
	private final ShrinkingSequence<T> toMap;
	private final Function<FalsificationResult<T>, FalsificationResult<U>> mapper;

	public MappedShrinkingSequence(ShrinkingSequence<T> toMap, Function<FalsificationResult<T>, FalsificationResult<U>> mapper) {this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public boolean next(Runnable count, Consumer<U> uReporter) {
		//TODO: This is really involved. See TODO in ShrinkingSequence.next()
		Consumer<T> tReporter = aT -> uReporter.accept(mapper.apply(FalsificationResult.falsified(Shrinkable.unshrinkable(aT))).value());
		return toMap.next(count, tReporter);
	}

	@Override
	public FalsificationResult<U> current() {
		return mapper.apply(toMap.current());
	}
}
