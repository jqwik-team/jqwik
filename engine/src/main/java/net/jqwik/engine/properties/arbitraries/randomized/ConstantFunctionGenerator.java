package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;

public class ConstantFunctionGenerator<F> extends AbstractFunctionGenerator<F> {

	public ConstantFunctionGenerator(Class<F> functionalType, RandomGenerator<?> resultGenerator) {
		super(functionalType, resultGenerator);
	}

	@Override
	public Shrinkable<F> next(Random random) {
		Shrinkable<?> shrinkableConstant = resultGenerator.next(random);
		return createConstantFunction(shrinkableConstant);
	}

}
