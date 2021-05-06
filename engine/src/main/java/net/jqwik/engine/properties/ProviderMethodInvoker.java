package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

class ProviderMethodInvoker {

	ProviderMethodInvoker(Object instance, SubtypeProvider subtypeProvider) {
		this.instance = instance;
		this.subtypeProvider = subtypeProvider;
	}

	private Object instance;
	private SubtypeProvider subtypeProvider;

	Set<Arbitrary<?>> invoke(Method providerMethod, TypeUsage targetType) {
		Parameter[] parameters = providerMethod.getParameters();
		Object[] arguments = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			arguments[i] = resolveParameter(parameters[i], providerMethod, targetType);
		}
		return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, instance, arguments));
	}

	protected Object resolveParameter(Parameter parameter, Method providerMethod, TypeUsage targetType) {
		if (parameter.getType().isAssignableFrom(TypeUsage.class)) {
			return targetType;
		} else if (parameter.getType().isAssignableFrom(SubtypeProvider.class)) {
			return subtypeProvider;
		} else {
			String message = String.format(
				"Parameter <%s> is not allowed in @Provide method <%s>.",
				parameter,
				providerMethod
			);
			throw new JqwikException(message);
		}
	}

	private Set<Arbitrary<?>> wrapInSet(Object result) {
		return Collections.singleton((Arbitrary<?>) result);
	}

}
