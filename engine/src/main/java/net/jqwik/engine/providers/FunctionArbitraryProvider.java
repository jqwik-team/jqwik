package net.jqwik.engine.providers;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class FunctionArbitraryProvider implements ArbitraryProvider {

	private static Class<?>[] SUPPORTED_FUNCTIONAL_TYPES =
		new Class[]{Function.class, Supplier.class, Consumer.class, Predicate.class};

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		for (Class<?> supportedType : SUPPORTED_FUNCTIONAL_TYPES) {
			if (targetType.canBeAssignedTo(TypeUsage.of(supportedType))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Class<?> functionalType = targetType.getRawType();
		TypeUsage returnType = getReturnType(targetType);

		return subtypeProvider
			.resolveAndCombine(returnType)
			.map(arbitraries -> {
				Arbitrary<?> resultArbitrary = arbitraries.get(0);
				return Functions.function(functionalType).returns(resultArbitrary);
			})
			.collect(Collectors.toSet());
	}

	private TypeUsage getReturnType(TypeUsage targetType) {
		Optional<Method> optionalMethod =
			JqwikReflectionSupport.getInterfaceMethod(targetType.getRawType());

		return optionalMethod
			.map(method -> {
				GenericsClassContext context = GenericsSupport.contextFor(targetType);
				TypeResolution typeResolution = context.resolveReturnType(method);
				return TypeUsage.forType(typeResolution.type());
			})
			.orElse(TypeUsage.of(Void.class));
	}

}
