package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.support.*;

abstract class AbstractFunctionGenerator<F, R> implements RandomGenerator<F> {
	final Class<F> functionalType;
	final RandomGenerator<R> resultGenerator;
	final List<Tuple2<Predicate<List>, Function<List, R>>> conditions;

	AbstractFunctionGenerator(
		Class<F> functionalType,
		RandomGenerator<R> resultGenerator,
		List<Tuple2<Predicate<List>, Function<List, R>>> conditions
	) {
		this.functionalType = functionalType;
		this.resultGenerator = resultGenerator;
		this.conditions = conditions;
	}

	F createFunctionProxy(InvocationHandler handler) {
		//noinspection unchecked
		return (F) Proxy.newProxyInstance(functionalType.getClassLoader(), new Class[]{functionalType}, handler);
	}


	Shrinkable<F> createConstantFunction(Shrinkable<R> shrinkableConstant) {
		return shrinkableConstant.map(this::constantFunction);
	}

	private F constantFunction(R constant) {
		InvocationHandler handler = (proxy, method, args) -> {
			if (JqwikReflectionSupport.isToStringMethod(method)) {
				return String.format(
					"Constant Function<%s>(%s)",
					functionalType.getSimpleName(),
					JqwikStringSupport.displayString(constant)
				);
			}
			return conditionalResult(args).orElse(constant);
		};
		return createFunctionProxy(handler);
	}

	Optional<R> conditionalResult(Object[] args) {
		Optional<R> conditionalResult = Optional.empty();
		for (Tuple2<Predicate<List>, Function<List, R>> condition : conditions) {
			List<Object> params = Arrays.asList(args);
			if (condition.get1().test(params)) {
				conditionalResult = Optional.of(condition.get2().apply(params));
			}
		}
		return conditionalResult;
	}
}
