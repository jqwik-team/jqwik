package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

class ProviderMethodInvoker {

	ProviderMethodInvoker(Object instance, ArbitraryProvider.SubtypeProvider subtypeProvider) {
		this.instance = instance;
		this.subtypeProvider = subtypeProvider;
	}

	private Object instance;
	private ArbitraryProvider.SubtypeProvider subtypeProvider;

	Set<Arbitrary<?>> invoke(Method providerMethod, TypeUsage targetType) {
		Parameter[] parameters = providerMethod.getParameters();
		if (parameters.length == 0) {
			return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, instance));
		}
		if (parameters[0].getType().isAssignableFrom(TypeUsage.class)) {
			if (parameters.length == 1) {
				return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, instance, targetType));
			}
		}
		if (parameters[1].getType().isAssignableFrom(ArbitraryProvider.SubtypeProvider.class)) {
			if (parameters.length == 2) {
				return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, instance, targetType, subtypeProvider));
			}
		}
		String message = String.format("Some of the parameters of %s are not allowed in provider methods", providerMethod);
		throw new JqwikException(message);
	}

	private Set<Arbitrary<?>> wrapInSet(Object result) {
		return Collections.singleton((Arbitrary<?>) result);
	}

}
