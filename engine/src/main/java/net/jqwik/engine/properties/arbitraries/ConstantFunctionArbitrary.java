package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class ConstantFunctionArbitrary<F> implements FunctionArbitrary<F> {

	private final Class<F> functionalType;
	private final Arbitrary<?> resultArbitrary;

	public ConstantFunctionArbitrary(Class<F> functionalType, Arbitrary<?> resultArbitrary) {
		this.functionalType = functionalType;
		this.resultArbitrary = resultArbitrary;
	}

	@Override
	public RandomGenerator<F> generator(int genSize) {
		return resultArbitrary.generator(genSize).map(this::constantFunction);
	}

	@Override
	public Optional<ExhaustiveGenerator<F>> exhaustive() {
		return resultArbitrary.exhaustive().map(generator -> generator.map(this::constantFunction));
	}

	private F constantFunction(Object constant) {
		InvocationHandler handler = (proxy, method, args) -> constant;
		//noinspection unchecked
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
	}
}
