package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface NShrinkableGenerator<T> {
	NShrinkable<T> next(Random random);

	default <U> NShrinkableGenerator<U> map(Function<T, U> mapper) {
		return random -> this.next(random).map(mapper);
	}

	default NShrinkableGenerator<T> filter(Predicate<T> filterPredicate) {
		return new NFilteredGenerator<>(this, filterPredicate);
	}

	default NShrinkableGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability)
				return NShrinkable.unshrinkable(null);
			return NShrinkableGenerator.this.next(random);
		};
	};

	default NShrinkableGenerator<T> withSamples(T...samples) {
		NShrinkableGenerator<T> samplesGenerator = NShrinkableGenerators.samples(samples);
		NShrinkableGenerator<T> generator = this;
		AtomicInteger tryCount = new AtomicInteger(0);
		return random -> {
			if (tryCount.getAndIncrement() < samples.length)
				return samplesGenerator.next(random);
			return generator.next(random);
		};
	}

	default NShrinkableGenerator<T> mixIn(NShrinkableGenerator<T> otherGenerator, double mixInProbability) {
		return random -> random.nextDouble() <= mixInProbability ? otherGenerator.next(random) : next(random) ;
	};



}
