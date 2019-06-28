package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class ConstantFunctionGenerator<F> implements RandomGenerator<F> {

	private final Class<F> functionalType;
	private final RandomGenerator<?> resultGenerator;

	public ConstantFunctionGenerator(Class<F> functionalType, RandomGenerator<?> resultGenerator) {
		this.functionalType = functionalType;
		this.resultGenerator = resultGenerator;
	}

	@Override
	public Shrinkable<F> next(Random random) {
		return resultGenerator.next(random).map(this::constantFunction);
	}

	private F constantFunction(Object constant) {
		InvocationHandler handler = (proxy, method, args) -> {
			if (JqwikReflectionSupport.isToStringMethod(method)) {
				return String.format(
					"Constant Function<%s>(%s)",
					functionalType.getSimpleName(),
					JqwikStringSupport.displayString(constant)
				);
			}
			return constant;
		};
		//noinspection unchecked
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
	}

}
