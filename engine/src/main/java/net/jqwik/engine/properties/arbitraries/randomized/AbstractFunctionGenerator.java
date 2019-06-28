package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

abstract class AbstractFunctionGenerator<F> implements RandomGenerator<F> {
	final Class<F> functionalType;
	final RandomGenerator<?> resultGenerator;

	AbstractFunctionGenerator(
		Class<F> functionalType,
		RandomGenerator<?> resultGenerator
	) {
		this.functionalType = functionalType;
		this.resultGenerator = resultGenerator;
	}

	F createFunctionProxy(InvocationHandler handler) {
		//noinspection unchecked
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
	}


	Shrinkable<F> createConstantFunction(Shrinkable<?> shrinkableConstant) {
		return shrinkableConstant.map(this::constantFunction);
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
		return createFunctionProxy(handler);
	}
}
