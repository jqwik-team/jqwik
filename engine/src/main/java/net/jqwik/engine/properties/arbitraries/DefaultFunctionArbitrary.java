package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultFunctionArbitrary<F, R> extends TypedCloneable implements FunctionArbitrary<F, R> {

	private final Class<F> functionalType;
	private final Arbitrary<R> resultArbitrary;
	private final List<Tuple2<Predicate<List<Object>>, Function<List<Object>, R>>> conditions = new ArrayList<>();

	public DefaultFunctionArbitrary(Class<F> functionalType, Arbitrary<R> resultArbitrary) {
		this.functionalType = functionalType;
		this.resultArbitrary = resultArbitrary;
	}

	@Override
	public RandomGenerator<F> generator(int genSize) {
		return RandomGenerators.oneOf(createGenerators(genSize, false));
	}

	@Override
	public RandomGenerator<F> generatorWithEmbeddedEdgeCases(int genSize) {
		return RandomGenerators.oneOf(createGenerators(genSize, true));
	}

	@Override
	public EdgeCases<F> edgeCases() {
		ConstantFunctionGenerator<F, R> constantFunctionGenerator = createConstantFunctionGenerator(1000, true);
		return EdgeCasesSupport.mapShrinkable(
				resultArbitrary.edgeCases(),
				constantFunctionGenerator::createConstantFunction
		);
	}

	private List<RandomGenerator<F>> createGenerators(int genSize, boolean withEmbeddedEdgeCases) {
		ConstantFunctionGenerator<F, R> constantFunctionGenerator = createConstantFunctionGenerator(genSize, withEmbeddedEdgeCases);
		FunctionGenerator<F, R> functionGenerator =
				new FunctionGenerator<>(functionalType, resultArbitrary.generator(genSize, withEmbeddedEdgeCases), conditions);
		return Arrays.asList(
				constantFunctionGenerator,
				functionGenerator,
				functionGenerator,
				functionGenerator,
				functionGenerator
		);
	}

	private ConstantFunctionGenerator<F, R> createConstantFunctionGenerator(final int genSize, boolean withEmbeddedEdgeCases) {
		return new ConstantFunctionGenerator<>(functionalType, resultArbitrary.generator(genSize, withEmbeddedEdgeCases), conditions);
	}

	@Override
	public <F_ extends F> FunctionArbitrary<F_, R> when(Predicate<List<Object>> parameterCondition, Function<List<Object>, R> answer) {
		DefaultFunctionArbitrary<F_, R> clone = typedClone();
		clone.conditions.addAll(this.conditions);
		clone.addCondition(Tuple.of(parameterCondition, answer));
		return clone;
	}

	private void addCondition(Tuple2<Predicate<List<Object>>, Function<List<Object>, R>> condition) {
		conditions.add(condition);
	}
}
