package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

/**
 * Is loaded through reflection in api module
 */
public class FunctionsFacadeImpl extends Functions.FunctionsFacade {

	@Override
	public void ensureFunctionalType(Class<?> functionalType) {
		if (!isFunctionalType(functionalType)) {
			throw new NotAFunctionalTypeException(functionalType);
		}
	}

	@Override
	public <F> FunctionArbitrary<F> constantFunction(Class<?> functionalType, Arbitrary<?> resultArbitrary) {
		//noinspection unchecked
		return (FunctionArbitrary<F>) new ConstantFunctionArbitrary<>(functionalType, resultArbitrary);
	}
}
