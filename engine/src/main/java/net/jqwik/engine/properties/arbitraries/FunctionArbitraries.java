package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;

public class FunctionArbitraries {

	public static <F> Arbitrary<F> functions(Class<F> functionalType, Arbitrary<?> resultArbitrary) {
		if (!isFunctionalType(functionalType)) {
			throw new NotAFunctionalTypeException(functionalType);
		}
		return new ConstantFunctionArbitrary<>(functionalType, resultArbitrary);
	}

	public static boolean isFunctionalType(Class<?> candidateType) {
		if (!candidateType.isInterface()) {
			return false;
		}
		return countInterfaceMethods(candidateType) == 1;
	}

	private static long countInterfaceMethods(Class<?> candidateType) {
		Method[] methods = candidateType.getMethods();
		return Arrays
				   .stream(methods)
				   .filter(m -> !m.isDefault() && !ModifierSupport.isStatic(m))
				   .count();
	}

}
