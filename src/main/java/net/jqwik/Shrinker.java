
package net.jqwik;

import org.junit.gen5.commons.util.Preconditions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class Shrinker<T> {

	private static final Logger LOG = Logger.getLogger(Shrinker.class.getName());
	private static final int MAX_SHRINKING_ATTEMPTS = 100;

	private final Generator<T> generator;

	public Shrinker(Generator<T> generator) {
		this.generator = generator;
	}

	T shrink(T initialFailingValue, Function<T, Boolean> evaluator) {
		T lastFailingValue = Utils.clone(initialFailingValue); //Cloning necessary in case the evaluator changes state
		Preconditions.notNull(initialFailingValue, "initialFailingValue must not be null");
		Preconditions.condition(!evaluator.apply(initialFailingValue), "evaluator must fail with initialFailingValue");

		int counter = 0;
		while (true) {
			if (counter++ >= MAX_SHRINKING_ATTEMPTS) //May 100 iterations for shrinking
				break;

			List<T> shrinkingOptions = generator.shrink(lastFailingValue);
			Optional<T> shrinkedValue = firstFailingValue(shrinkingOptions, evaluator);
			if (shrinkedValue.isPresent()) {
				lastFailingValue = shrinkedValue.get();
				continue;
			}
			break;
		}
		return lastFailingValue;
	}

	private Optional<T> firstFailingValue(List<T> shrinkingOptions, Function<T, Boolean> evaluator) {
		for (T shrinkedValue : shrinkingOptions) {
			if (!evaluator.apply(shrinkedValue))
				return Optional.of(shrinkedValue);
		}
		return Optional.empty();
	}
}
