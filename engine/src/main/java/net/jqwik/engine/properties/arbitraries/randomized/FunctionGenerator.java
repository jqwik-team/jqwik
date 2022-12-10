package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.JqwikRandom;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

public class FunctionGenerator<F, R> extends AbstractFunctionGenerator<F, R> {

	private final AtomicReference<Shrinkable<R>> lastResult = new AtomicReference<>();

	public FunctionGenerator(
		Class<F> functionalType,
		RandomGenerator<R> resultGenerator,
		List<Tuple2<Predicate<List<Object>>, Function<List<Object>, R>>> conditions
	) {
		super(functionalType, resultGenerator, conditions);
	}

	@Override
	public Shrinkable<F> next(JqwikRandom random) {
		return new ShrinkableFunction(createFunction(random));
	}

	private F createFunction(JqwikRandom random) {
		long baseSeed = random.nextLong();
		InvocationHandler handler = (proxy, method, args) -> {
			if (JqwikReflectionSupport.isEqualsMethod(method)) {
				return handleEqualsMethod(proxy, args);
			}
			if (JqwikReflectionSupport.isToStringMethod(method)) {
				return handleToString(baseSeed);
			}
			if (JqwikReflectionSupport.isHashCodeMethod(method)) {
				return handleHashCode((int) baseSeed);
			}
			if (method.isDefault()) {
				return handleDefaultMethod(proxy, method, args);
			}
			return conditionalResult(args).orElseGet(() -> {
				JqwikRandom randomForArgs = SourceOfRandomness.newRandom(seedForArgs(baseSeed, args));
				Shrinkable<R> shrinkableResult = resultGenerator.next(randomForArgs);
				storeLastResult(shrinkableResult);
				return new Object[]{shrinkableResult.value()};
			})[0];
		};
		return createFunctionProxy(handler);
	}

	private int handleHashCode(final int baseSeed) {
		return baseSeed;
	}

	private Object handleToString(final long baseSeed) {
		return String.format(
			"Function<%s>(baseSeed: %s)",
			functionalType.getSimpleName(),
			baseSeed
		);
	}

	private void storeLastResult(Shrinkable<R> result) {
		lastResult.set(result);
	}

	private long seedForArgs(long baseSeed, Object[] args) {
		long seed = baseSeed;
		if (args != null) {
			for (Object arg : args) {
				seed = Long.rotateRight(seed, 16);
				if (arg != null) {
					seed ^= arg.hashCode();
				}
			}
		}
		return seed;
	}

	private class ShrinkableFunction implements Shrinkable<F> {

		private final F value;

		private ShrinkableFunction(F function) {
			value = function;
		}

		@Override
		public F value() {
			return value;
		}

		@Override
		public Stream<Shrinkable<F>> shrink() {
			if (lastResult.get() == null) {
				return Stream.empty();
			}
			Shrinkable<F> constantFunction = createConstantFunction(lastResult.get());
			return Stream.of(constantFunction);
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.MAX;
		}
	}
}
