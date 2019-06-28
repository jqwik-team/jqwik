package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultFunctionArbitrary<F> implements FunctionArbitrary<F> {

	private final Class<F> functionalType;
	private final Arbitrary<?> resultArbitrary;

	public DefaultFunctionArbitrary(Class<F> functionalType, Arbitrary<?> resultArbitrary) {
		this.functionalType = functionalType;
		this.resultArbitrary = resultArbitrary;
	}

	@Override
	public RandomGenerator<F> generator(int genSize) {
		return RandomGenerators.oneOf(createGenerators(genSize));
	}

	private List<RandomGenerator<F>> createGenerators(int genSize) {
		ConstantFunctionGenerator<F> constantFunctionGenerator =
			new ConstantFunctionGenerator<>(functionalType, resultArbitrary.generator(genSize));
		FunctionGenerator<F> functionGenerator =
			new FunctionGenerator<>(functionalType, resultArbitrary.generator(genSize));
		return Arrays.asList(
			constantFunctionGenerator,
			functionGenerator,
			functionGenerator,
			functionGenerator,
			functionGenerator
		);
	}
}
