package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface RandomGenerator<T> {
	Shrinkable<T> next(Random random);

	default <U> RandomGenerator<U> map(Function<T, U> mapper) {
		return random -> this.next(random).map(mapper);
	}

	default RandomGenerator<T> filter(Predicate<T> filterPredicate) {
		return new NFilteredGenerator<>(this, filterPredicate);
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability)
				return Shrinkable.unshrinkable(null);
			return RandomGenerator.this.next(random);
		};
	};

	default RandomGenerator<T> withSamples(T...samples) {
		RandomGenerator<T> samplesGenerator = NShrinkableGenerators.samples(samples);
		RandomGenerator<T> generator = this;
		AtomicInteger tryCount = new AtomicInteger(0);
		return random -> {
			if (tryCount.getAndIncrement() < samples.length)
				return samplesGenerator.next(random);
			return generator.next(random);
		};
	}

	default RandomGenerator<T> mixIn(RandomGenerator<T> otherGenerator, double mixInProbability) {
		return random -> random.nextDouble() <= mixInProbability ? otherGenerator.next(random) : next(random) ;
	};



}
