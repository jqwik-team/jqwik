package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class ConstantFunctionGenerator<F, R> extends AbstractFunctionGenerator<F, R> {

	public ConstantFunctionGenerator(
		Class<F> functionalType,
		RandomGenerator<R> resultGenerator,
		List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> conditions
	) {
		super(functionalType, resultGenerator, conditions);
	}

	@Override
	public Shrinkable<F> next(Random random) {
		Shrinkable<R> shrinkableConstant = resultGenerator.next(random);
		return createConstantFunction(shrinkableConstant);
	}

}
