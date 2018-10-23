package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class ContainerShrinkingSequence<C, E> implements ShrinkingSequence<C> {
	private final ElementsShrinkingSequence<E> elementsSequence;
	private final Function<Shrinkable<List<E>>, Shrinkable<C>> toContainerShrinkable;

	ContainerShrinkingSequence(
		List<Shrinkable<E>> currentElements,
		Falsifier<List<E>> listFalsifier,
		Function<List<Shrinkable<E>>, ShrinkingDistance> distanceFunction,
		Function<Shrinkable<List<E>>, Shrinkable<C>> toContainerShrinkable
	) {
		this.toContainerShrinkable = toContainerShrinkable;
		elementsSequence = new ElementsShrinkingSequence<>(currentElements, null, listFalsifier, distanceFunction);
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<C>> falsifiedReporter) {
		Consumer<FalsificationResult<List<E>>> listReporter =
			listResult -> falsifiedReporter.accept(toContainerResult(listResult));
		return elementsSequence.next(count, listReporter);
	}

	private FalsificationResult<C> toContainerResult(FalsificationResult<List<E>> listResult) {
		return listResult.map(toContainerShrinkable);
	}

	@Override
	public FalsificationResult<C> current() {
		return toContainerResult(elementsSequence.current());
	}
}
