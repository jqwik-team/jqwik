package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.*;

import org.jspecify.annotations.*;

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

	@SuppressWarnings("unchecked")
	@Override
	public <F, R extends @Nullable Object> FunctionArbitrary<F, R> function(Class<?> functionalType, Arbitrary<R> resultArbitrary) {
		return (FunctionArbitrary<F, R>) new DefaultFunctionArbitrary<>(functionalType, resultArbitrary);
	}
}
