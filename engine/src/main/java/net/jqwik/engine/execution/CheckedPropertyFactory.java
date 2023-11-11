package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class CheckedPropertyFactory {

	public CheckedProperty fromDescriptor(
		PropertyMethodDescriptor propertyMethodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry,
		ResolveParameterHook parameterResolver,
		InvokePropertyMethodHook invokeMethod
	) {
		String propertyName = propertyMethodDescriptor.extendedLabel();

		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		PropertyConfiguration configuration = propertyMethodDescriptor.getConfiguration();

		TryLifecycleExecutor tryLifecycleExecutor = createTryExecutor(propertyMethodDescriptor, propertyLifecycleContext, aroundTry, invokeMethod);
		List<MethodParameter> propertyParameters = extractParameters(propertyMethod, propertyMethodDescriptor.getContainerClass());

		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(
			propertyLifecycleContext.testInstances(),
			CurrentDomainContext.get()
		);

		Optional<Iterable<? extends Tuple>> optionalData =
			new PropertyMethodDataResolver(propertyMethodDescriptor.getContainerClass(), propertyLifecycleContext.testInstances())
				.forMethod(propertyMethodDescriptor.getTargetMethod());

		return new CheckedProperty(
			propertyName,
			tryLifecycleExecutor,
			propertyParameters,
			new CachingArbitraryResolver(arbitraryResolver),
			parameterResolver,
			propertyLifecycleContext,
			optionalData,
			configuration
		);
	}

	private TryLifecycleExecutor createTryExecutor(
		PropertyMethodDescriptor propertyMethodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry,
		InvokePropertyMethodHook invokeMethod
	) {
		AroundTryHook aroundTryWithFinishing = (context, aTry, parameters) -> {
			try {
				return aroundTry.aroundTry(context, aTry, parameters);
			} finally {
				StoreRepository.getCurrent().finishTry(propertyMethodDescriptor);
			}
		};

		TryExecutor rawExecutor = createRawExecutor(propertyLifecycleContext, invokeMethod);
		return new AroundTryLifecycle(rawExecutor, aroundTryWithFinishing);
	}

	private TryExecutor createRawExecutor(
		PropertyLifecycleContext propertyLifecycleContext,
		InvokePropertyMethodHook invokeMethod
	) {
		return createRawFunction(propertyLifecycleContext, invokeMethod);
	}

	private CheckedFunction createRawFunction(
		PropertyLifecycleContext propertyLifecycleContext,
		InvokePropertyMethodHook invokeMethod
	) {
		Method targetMethod = propertyLifecycleContext.targetMethod();
		Function<List<Object>, Object> function = params -> {
			try {
				return invokeMethod.invoke(targetMethod, propertyLifecycleContext.testInstance(), params.toArray());
			} catch (Throwable e) {
				return JqwikExceptionSupport.throwAsUncheckedException(e);
			}
		};

		return params -> {
			Object result = function.apply(params);
			return result == null || !Boolean.FALSE.equals(result);
		};
	}

	private List<MethodParameter> extractParameters(Method targetMethod, Class<?> containerClass) {
		return JqwikReflectionSupport.getMethodParameters(targetMethod, containerClass);
	}

}
