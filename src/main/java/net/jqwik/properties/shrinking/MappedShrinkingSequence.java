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
	public boolean next(Runnable count, Consumer<FalsificationResult<U>> uReporter) {
		Consumer<FalsificationResult<T>> tReporter = tResult -> uReporter.accept(mapper.apply(tResult));
		return toMap.next(count, tReporter);
	}

	@Override
	public void init(FalsificationResult<U> initialCurrent) {
		FalsificationResult<T> toMapCurrent = toMap.current();
		if (toMapCurrent != null) {
			toMap.init(FalsificationResult.falsified(toMapCurrent.shrinkable(), initialCurrent.throwable().orElse(null)));
		}
	}

	@Override
	public FalsificationResult<U> current() {
		return mapper.apply(toMap.current());
	}
}
