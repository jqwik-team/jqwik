package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class CheckedPropertyFactory {

	private static List<Class<?>> BOOLEAN_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	public CheckedProperty fromDescriptor(
		PropertyMethodDescriptor propertyMethodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry,
		ResolveParameterHook parameterResolver
	) {
		String propertyName = propertyMethodDescriptor.extendedLabel();

		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		PropertyConfiguration configuration = propertyMethodDescriptor.getConfiguration();

		AroundTryLifecycle tryExecutor = createTryExecutor(propertyMethodDescriptor, propertyLifecycleContext, aroundTry);
		List<MethodParameter> propertyParameters = extractParameters(propertyMethod, propertyMethodDescriptor.getContainerClass());

		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(
			propertyMethodDescriptor.getContainerClass(),
			propertyLifecycleContext.testInstance(),
			DomainContextFacadeImpl.getCurrentContext()
		);

		Optional<Iterable<? extends Tuple>> optionalData =
			new PropertyMethodDataResolver(propertyMethodDescriptor.getContainerClass(), propertyLifecycleContext.testInstance())
				.forMethod(propertyMethodDescriptor.getTargetMethod());

		return new CheckedProperty(
			propertyName,
			tryExecutor,
			propertyParameters,
			new CachingArbitraryResolver(arbitraryResolver),
			parameterResolver,
			optionalData,
			configuration
		);
	}

	private AroundTryLifecycle createTryExecutor(
		PropertyMethodDescriptor propertyMethodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		AroundTryHook aroundTryWithFinishing = (context, aTry, parameters) -> {
			try {
				return aroundTry.aroundTry(context, aTry, parameters);
			} finally {
				StoreRepository.getCurrent().finishTry(propertyMethodDescriptor);
			}
		};

		TryExecutor rawExecutor = createRawExecutor(propertyMethodDescriptor, propertyLifecycleContext.testInstance());
		return new AroundTryLifecycle(rawExecutor, propertyLifecycleContext, aroundTryWithFinishing);
	}

	private TryExecutor createRawExecutor(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		return createRawFunction(propertyMethodDescriptor, testInstance);
	}

	private CheckedFunction createRawFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		Method targetMethod = propertyMethodDescriptor.getTargetMethod();
		Class<?> returnType = targetMethod.getReturnType();
		Function<List<Object>, Object> function = params -> ReflectionSupport.invokeMethod(targetMethod, testInstance, params.toArray());

		if (BOOLEAN_RETURN_TYPES.contains(returnType))
			return params -> (boolean) function.apply(params);
		else
			return params -> {
				function.apply(params);
				return true;
			};
	}

	private List<MethodParameter> extractParameters(Method targetMethod, Class<?> containerClass) {
		return JqwikReflectionSupport.getMethodParameters(targetMethod, containerClass);
	}

}
