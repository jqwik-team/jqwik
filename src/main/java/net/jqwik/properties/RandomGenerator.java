package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public interface RandomGenerator<T> {

	T next(Random random);

	default RandomGenerator<T> filter(Predicate<? super T> predicate) {
		return random -> {
			while (true) {
				T value = RandomGenerator.this.next(random);
				if (predicate.test(value))
					return value;
			}
		};
	};

	default <U> RandomGenerator<U> map(Function<? super T, ? extends U> mapper) {
		return random -> mapper.apply(RandomGenerator.this.next(random));
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability)
				return null;
			return RandomGenerator.this.next(random);
		};
	};

	default RandomGenerator<T> withSamples(T...samples) {
		RandomGenerator<T> samplesGenerator = RandomGenerators.samples(samples);
		RandomGenerator<T> generator = this;
		AtomicInteger tryCount = new AtomicInteger(0);
		return random -> {
			if (tryCount.getAndIncrement() < samples.length)
				return samplesGenerator.next(random);
			return generator.next(random);
		};
	}
}
