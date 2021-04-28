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

	private static final List<Class<?>> BOOLEAN_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	public CheckedProperty fromDescriptor(
		PropertyMethodDescriptor propertyMethodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry,
		ResolveParameterHook parameterResolver
	) {
		String propertyName = propertyMethodDescriptor.extendedLabel();

		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		PropertyConfiguration configuration = propertyMethodDescriptor.getConfiguration();

		TryLifecycleExecutor tryLifecycleExecutor = createTryExecutor(propertyMethodDescriptor, propertyLifecycleContext, aroundTry);
		List<MethodParameter> propertyParameters = extractParameters(propertyMethod, propertyMethodDescriptor.getContainerClass());

		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(
			propertyLifecycleContext.testInstance(),
			DomainContextFacadeImpl.getCurrentContext()
		);

		Optional<Iterable<? extends Tuple>> optionalData =
			new PropertyMethodDataResolver(propertyMethodDescriptor.getContainerClass(), propertyLifecycleContext.testInstance())
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
		AroundTryHook aroundTry
	) {
		AroundTryHook aroundTryWithFinishing = (context, aTry, parameters) -> {
			try {
				return aroundTry.aroundTry(context, aTry, parameters);
			} finally {
				StoreRepository.getCurrent().finishTry(propertyMethodDescriptor);
			}
		};

		TryExecutor rawExecutor = createRawExecutor(propertyLifecycleContext);
		return new AroundTryLifecycle(rawExecutor, aroundTryWithFinishing);
	}

	private TryExecutor createRawExecutor(PropertyLifecycleContext propertyLifecycleContext) {
		return createRawFunction(propertyLifecycleContext);
	}

	private CheckedFunction createRawFunction(PropertyLifecycleContext propertyLifecycleContext) {
		Method targetMethod = propertyLifecycleContext.targetMethod();
		Class<?> returnType = targetMethod.getReturnType();
		Function<List<Object>, Object> function = params -> ReflectionSupport.invokeMethod(targetMethod, propertyLifecycleContext.testInstance(), params.toArray());

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
