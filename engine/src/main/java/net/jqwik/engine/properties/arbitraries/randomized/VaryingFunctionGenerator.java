package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;

public class VaryingFunctionGenerator<F> implements RandomGenerator<F> {

	private final Class<F> functionalType;
	private final RandomGenerator<?> resultGenerator;

	public VaryingFunctionGenerator(Class<F> functionalType, RandomGenerator<?> resultGenerator) {
		this.functionalType = functionalType;
		this.resultGenerator = resultGenerator;
	}

	@Override
	public Shrinkable<F> next(Random random) {
		return Shrinkable.unshrinkable(constantFunction(random.nextLong()));
	}

	private F constantFunction(long baseSeed) {
		InvocationHandler handler = (proxy, method, args) -> {
			Random randomForArgs = new Random(seedForArgs(baseSeed, args));
			return resultGenerator.next(randomForArgs).value();
		};
		//noinspection unchecked
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
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

}
