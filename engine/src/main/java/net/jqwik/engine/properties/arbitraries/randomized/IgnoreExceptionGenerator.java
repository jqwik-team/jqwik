package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class IgnoreExceptionGenerator<T> implements RandomGenerator<T> {

	private final RandomGenerator<T> base;
	private final Class<? extends Throwable> exceptionType;

	public IgnoreExceptionGenerator(RandomGenerator<T> base, Class<? extends Throwable> exceptionType) {
		this.base = base;
		this.exceptionType = exceptionType;
	}

	@Override
	public Shrinkable<T> next(final Random random) {
		return new IgnoreExceptionShrinkable(nextUntilAccepted(random, base::next));
	}

	private Shrinkable<T> nextUntilAccepted(Random random, Function<Random, Shrinkable<T>> fetchShrinkable) {
		return MaxTriesLoop.loop(
			() -> true,
			next -> {
				try {
					next = fetchShrinkable.apply(random);
					// Enforce value generation for possible exception raising
					next.value();
					return Tuple.of(true, next);
				} catch (Throwable throwable) {
					if (exceptionType.isAssignableFrom(throwable.getClass())) {
						return Tuple.of(false, next);
					}
					throw throwable;
				}
			},
			(maxMisses) -> {
				String message = String.format("%s missed more than %s times.", toString(), maxMisses);
				return new TooManyFilterMissesException(message);
			}
		);
	}

	private class IgnoreExceptionShrinkable implements Shrinkable<T> {

		private final Shrinkable<T> shrinkable;

		private IgnoreExceptionShrinkable(Shrinkable<T> shrinkable) {
			this.shrinkable = shrinkable;
		}

		@Override
		public T value() {
			return shrinkable.value();
		}

		@Override
		public Stream<Shrinkable<T>> shrink() {
			return shrinkable.shrink().filter(s -> {
				try {
					s.value();
					return true;
				} catch (Throwable throwable) {
					if (exceptionType.isAssignableFrom(throwable.getClass())) {
						return false;
					}
					throw throwable;
				}
			}).map(IgnoreExceptionShrinkable::new);
		}

		@Override
		public ShrinkingDistance distance() {
			return shrinkable.distance();
		}
	}
}
