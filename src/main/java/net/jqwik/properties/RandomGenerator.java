package net.jqwik.properties;

import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public interface RandomGenerator<T> {

	/**
		This method generates the next random value wrapped within the Shrinkable interface.
	    The method must ALWAYS return a next value.
	 */
	Shrinkable<T> next(Random random);

	default <U> RandomGenerator<U> map(Function<T, U> mapper) {
		return random -> this.next(random).map(mapper);
	}

	default RandomGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredGenerator<>(this, filterPredicate);
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability)
				return Shrinkable.unshrinkable(null);
			return RandomGenerator.this.next(random);
		};
	};

	default RandomGenerator<T> withSamples(Shrinkable<T>... samples) {
		return withSamples(Arrays.asList(samples));
	}

	default RandomGenerator<T> withSamples(List<Shrinkable<T>> samples) {
		RandomGenerator<T> samplesGenerator = RandomGenerators.samples(samples);
		RandomGenerator<T> generator = this;
		AtomicInteger tryCount = new AtomicInteger(0);
		return random -> {
			if (tryCount.getAndIncrement() < samples.size())
				return samplesGenerator.next(random);
			return generator.next(random);
		};
	}

	default RandomGenerator<T> mixIn(RandomGenerator<T> otherGenerator, double mixInProbability) {
		return random -> random.nextDouble() <= mixInProbability ? otherGenerator.next(random) : next(random) ;
	};

}
