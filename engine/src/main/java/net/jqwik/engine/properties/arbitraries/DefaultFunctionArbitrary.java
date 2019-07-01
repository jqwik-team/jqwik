package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultFunctionArbitrary<F, R> extends AbstractArbitraryBase implements FunctionArbitrary<F, R>  {

	private final Class<F> functionalType;
	private final Arbitrary<R> resultArbitrary;
	private final List<Tuple2<Predicate<List>, Function<List, R>>> conditions = new ArrayList<>();

	public DefaultFunctionArbitrary(Class<F> functionalType, Arbitrary<R> resultArbitrary) {
		this.functionalType = functionalType;
		this.resultArbitrary = resultArbitrary;
	}

	@Override
	public RandomGenerator<F> generator(int genSize) {
		return RandomGenerators.oneOf(createGenerators(genSize));
	}

	private List<RandomGenerator<F>> createGenerators(int genSize) {
		ConstantFunctionGenerator<F, R> constantFunctionGenerator =
			new ConstantFunctionGenerator<>(functionalType, resultArbitrary.generator(genSize), conditions);
		FunctionGenerator<F, R> functionGenerator =
			new FunctionGenerator<>(functionalType, resultArbitrary.generator(genSize), conditions);
		return Arrays.asList(
			constantFunctionGenerator,
			functionGenerator,
			functionGenerator,
			functionGenerator,
			functionGenerator
		);
	}

	@Override
	// TODO: Is there a way to map F on F_ safely?
	public <F_> FunctionArbitrary<F_, R> when(Predicate<List> parameterCondition, Function<List, R> answer) {
		DefaultFunctionArbitrary<F_, R> clone = typedClone();
		clone.conditions.addAll(this.conditions);
		clone.addCondition(Tuple.of(parameterCondition, answer));
		return clone;
	}

	private void addCondition(Tuple2<Predicate<List>, Function<List, R>> condition) {
		conditions.add(condition);
	}
}
