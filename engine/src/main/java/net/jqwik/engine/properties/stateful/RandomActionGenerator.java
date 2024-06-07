package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import org.jspecify.annotations.*;

class RandomActionGenerator<T extends @Nullable Object> implements ActionGenerator<T> {

	private static final int MAX_TRIES = 1000;

	private final RandomGenerator<Action<T>> randomGenerator;
	private final Random random;
	private List<Shrinkable<Action<T>>> shrinkableActions = new ArrayList<>();

	RandomActionGenerator(Arbitrary<Action<T>> actionArbitrary, int genSize, Random random) {
		this.random = random;
		this.randomGenerator = actionArbitrary.generator(genSize);
	}

	@Override
	public Action<T> next(T model) {
		int tries = 0;
		while (tries++ < MAX_TRIES) {
			Shrinkable<Action<T>> shrinkable = randomGenerator.next(random);
			boolean precondition = shrinkable.value().precondition(model);
			if (!precondition) {
				continue;
			}
			shrinkableActions.add(shrinkable);
			return shrinkable.value();
		}
		String message = String.format("Could not find action with succeeding precondition after %s tries", tries);
		throw new NoSuchElementException(message);
	}

	@Override
	public List<Shrinkable<Action<T>>> generated() {
		return shrinkableActions;
	}
}
