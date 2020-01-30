package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class ContainerShrinkingSequence<C, E> implements ShrinkingSequence<C> {
	private final ShrinkingSequence<List<E>> elementsSequence;
	private final Function<Shrinkable<List<E>>, Shrinkable<C>> toContainerShrinkable;
	private FalsificationResult<C> currentResult;

	ContainerShrinkingSequence(
		List<Shrinkable<E>> currentElements,
		Falsifier<List<E>> listFalsifier,
		Function<List<Shrinkable<E>>, ShrinkingDistance> distanceFunction,
		Function<Shrinkable<List<E>>, Shrinkable<C>> toContainerShrinkable
	) {
		this.toContainerShrinkable = toContainerShrinkable;
		elementsSequence = new ShrinkElementsSequence<>(
			currentElements,
			listFalsifier,
			distanceFunction
		);
	}

	@Override
	public void init(FalsificationResult<C> initialCurrent) {
		if (currentResult == null) {
			currentResult = initialCurrent;
		} else {
			currentResult = FalsificationResult.falsified(currentResult.shrinkable(), initialCurrent.throwable().orElse(null));
		}
		// Only throwable is used in elementsSequence
		elementsSequence.init(FalsificationResult.falsified(
			Shrinkable.unshrinkable(new ArrayList<>()),
			initialCurrent.throwable().orElse(null)
		));
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<C>> falsifiedReporter) {
		Consumer<FalsificationResult<List<E>>> listReporter =
			listResult -> falsifiedReporter.accept(toContainerResult(listResult));
		boolean next = elementsSequence.next(count, listReporter);
		if (next) {
			this.currentResult = toContainerResult(elementsSequence.current());
		}
		return next;
	}

	private FalsificationResult<C> toContainerResult(FalsificationResult<List<E>> listResult) {
		return listResult.map(toContainerShrinkable);
	}

	@Override
	public FalsificationResult<C> current() {
		return currentResult;
	}
}
