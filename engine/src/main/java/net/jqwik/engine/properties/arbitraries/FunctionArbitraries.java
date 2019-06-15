package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class FunctionArbitraries {

	public static <F> Arbitrary<F> functions(Class<F> functionalType, Arbitrary<?> resultArbitrary) {
		if (!isFunctionalType(functionalType)) {
			throw new NotAFunctionalTypeException(functionalType);
		}
		return new ConstantFunctionArbitrary<>(functionalType, resultArbitrary);
	}

}
