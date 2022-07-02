package net.jqwik.engine.providers;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

public class FunctionArbitraryProvider implements ArbitraryProvider {

	private static final Class<?>[] EXCLUDE_TYPES =
		new Class[]{Iterable.class, Comparable.class};

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!JqwikReflectionSupport.isFunctionalType(targetType.getRawType())) {
			return false;
		}
		for (Class<?> excludedType : EXCLUDE_TYPES) {
			if (targetType.isOfType(excludedType)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Class<?> functionalType = targetType.getRawType();
		TypeUsage returnType = getReturnType(targetType);

		return subtypeProvider
				   .resolveAndCombine(returnType)
				   .map(arbitraries -> {
					   Arbitrary<?> resultArbitrary = arbitraries.get(0);
					   return Functions.function(functionalType).returning(resultArbitrary);
				   })
				   .collect(CollectorsSupport.toLinkedHashSet());
	}

	private TypeUsage getReturnType(TypeUsage targetType) {
		Optional<Method> optionalMethod =
			JqwikReflectionSupport.getFunctionMethod(targetType.getRawType());

		return optionalMethod
				   .map(method -> {
					   GenericsClassContext context = GenericsSupport.contextFor(targetType);
					   TypeResolution typeResolution = context.resolveReturnType(method);
					   return TypeUsageImpl.forResolution(typeResolution);
				   })
				   .orElse(TypeUsage.of(Void.class));
	}

	@Override
	public int priority() {
		// Give specialized providers priority
		return -1;
	}
}
