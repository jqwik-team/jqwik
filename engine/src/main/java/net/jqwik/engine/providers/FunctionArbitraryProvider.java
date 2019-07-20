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
		TypeUsage returnType = optionalMethod
			.map(method -> TypeUsage.forType(method.getGenericReturnType()))
			.orElse(TypeUsage.of(Void.class));

		// This does not match type variable names but just goes backward
		//  through all argument types and those of super types.
		//  This seems to work for all functional types in java.util.function.
		//  Use GenericSupport if you want to support any functional type
		List<TypeUsage> argumentTypes = collectTypeArguments(targetType);
		for (int i = argumentTypes.size() - 1; i >= 0; i--) {
			TypeUsage candidate = argumentTypes.get(i);
			if (candidate.canBeAssignedTo(returnType))
				return candidate;
		}
		return returnType;
	}

	private List<TypeUsage> collectTypeArguments(TypeUsage target) {
		ArrayList<TypeUsage> allTypeArguments = new ArrayList<>();
		allTypeArguments.addAll(target.getTypeArguments());
		for (Type anInterface : target.getRawType().getGenericInterfaces()) {
			allTypeArguments.addAll(TypeUsage.forType(anInterface).getTypeArguments());
		}
		return allTypeArguments;
	}

}
