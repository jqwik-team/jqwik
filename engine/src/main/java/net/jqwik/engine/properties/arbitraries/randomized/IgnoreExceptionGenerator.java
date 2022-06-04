package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

public class IgnoreExceptionGenerator<T> implements RandomGenerator<T> {

	private final RandomGenerator<T> base;
	private final Class<? extends Throwable> exceptionType;

	public IgnoreExceptionGenerator(RandomGenerator<T> base, Class<? extends Throwable> exceptionType) {
		this.base = base;
		this.exceptionType = exceptionType;
	}

	@Override
	public Shrinkable<T> next(final Random random) {
		return new IgnoreExceptionShrinkable<>(nextUntilAccepted(random, base::next), exceptionType);
	}

	private Shrinkable<T> nextUntilAccepted(Random random, Function<Random, Shrinkable<T>> fetchShrinkable) {
		for (int i = 0; i < 10000; i++) {
			try {
				Shrinkable<T> next = fetchShrinkable.apply(random);
				// Enforce value generation for possible exception raising
				next.value();
				return next;
			} catch (Throwable throwable) {
				if (exceptionType.isInstance(throwable)) {
					continue;
				}
				throw throwable;
			}
		}
		String message = String.format("%s missed more than %s times.", toString(), 10000);
		throw new TooManyFilterMissesException(message);
	}

}
