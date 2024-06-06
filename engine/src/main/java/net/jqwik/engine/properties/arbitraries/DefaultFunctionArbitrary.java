package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultFunctionArbitrary<F, R> extends TypedCloneable implements FunctionArbitrary<F, R> {

	private final Class<F> functionalType;
	private final Arbitrary<R> resultArbitrary;
	private final List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> conditions = new ArrayList<>();

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
	public EdgeCases<F> edgeCases(int maxEdgeCases) {
		ConstantFunctionGenerator<F, R> constantFunctionGenerator = createConstantFunctionGenerator(1000, true);
		return EdgeCasesSupport.mapShrinkable(
			resultArbitrary.edgeCases(maxEdgeCases),
			constantFunctionGenerator::createConstantFunction
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultFunctionArbitrary<F, R> that = (DefaultFunctionArbitrary<F, R>) o;
		if (!functionalType.equals(that.functionalType)) return false;
		if (!resultArbitrary.equals(that.resultArbitrary)) return false;
		return conditionsAreEqual(conditions, that.conditions);
	}

	private boolean conditionsAreEqual(
		List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> left,
		List<Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>>> right
	) {
		if (left.size() != right.size()) {
			return false;
		}
		for (int i = 0; i < left.size(); i++) {
			Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>> leftCondition = left.get(i);
			Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>> rightCondition = right.get(i);
			if (!LambdaSupport.areEqual(leftCondition.get1(), rightCondition.get1())) {
				return false;
			}
			if (!LambdaSupport.areEqual(leftCondition.get2(), rightCondition.get2())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(functionalType, resultArbitrary);
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
	public <F_ extends F> FunctionArbitrary<F_, R> when(Predicate<? super List<?>> parameterCondition, Function<? super List<?>, ? extends R> answer) {
		DefaultFunctionArbitrary<F_, R> clone = typedClone();
		clone.conditions.addAll(this.conditions);
		clone.addCondition(Tuple.of(parameterCondition, answer));
		return clone;
	}

	private void addCondition(Tuple2<Predicate<? super List<?>>, Function<? super List<?>, ? extends R>> condition) {
		conditions.add(condition);
	}
}
