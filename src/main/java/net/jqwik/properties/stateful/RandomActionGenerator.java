package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class RandomActionGenerator<T> implements ActionGenerator<T> {

	private final RandomGenerator<Action<T>> randomGenerator;
	private final Random random;
	private List<Shrinkable<Action<T>>> shrinkableActions = new ArrayList<>();

	public RandomActionGenerator(Arbitrary<Action<T>> actionArbitrary, int genSize, Random random) {
		this.random = random;
		this.randomGenerator = actionArbitrary.generator(genSize);
	}

	@Override
	public Action<T> next(T model) {
		while (true) {
			Shrinkable<Action<T>> shrinkable = randomGenerator.next(random);
			boolean precondition = shrinkable.value().precondition(model);
			if (!precondition) {
				continue;
			}
			shrinkableActions.add(shrinkable);
			return shrinkable.value();
		}
	}

	@Override
	public List<Shrinkable<Action<T>>> generated() {
		return shrinkableActions;
	}
}
