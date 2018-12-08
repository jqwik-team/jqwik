package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

public class MappedValueShrinkingSequence<T, U> implements ShrinkingSequence<U> {

	private final ShrinkingSequence<T> toMap;
	private final Function<T, U> mapper;

	public MappedValueShrinkingSequence(ShrinkingSequence<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<U>> falsifiedReporter) {
		return toMap.next(count, tResult -> falsifiedReporter.accept(tResult.map(shrinkable -> shrinkable.map(mapper))));
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
		return toMap.current().map(shrinkable -> shrinkable.map(mapper));
	}
}
