package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class FunctionGenerator<F> extends AbstractFunctionGenerator<F> {

	private final AtomicReference<Shrinkable<?>> lastResult = new AtomicReference<>();

	public FunctionGenerator(Class<F> functionalType, RandomGenerator<?> resultGenerator) {
		super(functionalType, resultGenerator);
	}

	@Override
	public Shrinkable<F> next(Random random) {
		return new ShrinkableFunction(createFunction(random));
	}

	private F createFunction(Random random) {
		long baseSeed = random.nextLong();
		InvocationHandler handler = (proxy, method, args) -> {
			if (JqwikReflectionSupport.isToStringMethod(method)) {
				return String.format(
					"Function<%s>(baseSeed: %s)",
					functionalType.getSimpleName(),
					baseSeed
				);
			}
			Random randomForArgs = new Random(seedForArgs(baseSeed, args));
			Shrinkable<?> shrinkableResult = resultGenerator.next(randomForArgs);
			storeLastResult(shrinkableResult);
			return shrinkableResult.value();
		};
		return createFunctionProxy(handler);
	}

	private void storeLastResult(Shrinkable<?> result) {
		lastResult.set(result);
	}

	private long seedForArgs(long baseSeed, Object[] args) {
		long seed = baseSeed;
		if (args != null) {
			for (Object arg : args) {
				seed = Long.rotateRight(seed, 16);
				seed ^= arg.hashCode();
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
		public ShrinkingSequence<F> shrink(Falsifier<F> falsifier) {
			if (lastResult.get() == null) {
				return ShrinkingSequence.dontShrink(this);
			}
			Shrinkable<F> constantFunction = createConstantFunction(lastResult.get());
			return constantFunction.shrink(falsifier);
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.MAX;
		}
	}
}
