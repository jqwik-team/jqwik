package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
		AroundTryHook aroundTry
	) {
		String propertyName = propertyMethodDescriptor.extendedLabel();

		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		PropertyConfiguration configuration = propertyMethodDescriptor.getConfiguration();

		CheckedFunction rawFunction = createRawFunction(propertyMethodDescriptor, propertyLifecycleContext.testInstance());
		CheckedFunction checkedFunction = new AroundTryLifecycle(rawFunction, propertyLifecycleContext, aroundTry);
		List<MethodParameter> forAllParameters = extractForAllParameters(propertyMethod, propertyMethodDescriptor.getContainerClass());

		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(
			propertyMethodDescriptor.getContainerClass(), propertyLifecycleContext.testInstance(),
			DomainContextFacadeImpl.getCurrentContext()
		);

		Optional<Iterable<? extends Tuple>> optionalData =
			new PropertyMethodDataResolver(propertyMethodDescriptor.getContainerClass(), propertyLifecycleContext.testInstance())
				.forMethod(propertyMethodDescriptor.getTargetMethod());

		return new CheckedProperty(
			propertyName,
			checkedFunction,
			forAllParameters,
			new CachingArbitraryResolver(arbitraryResolver),
			optionalData,
			configuration
		);
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

	private List<MethodParameter> extractForAllParameters(Method targetMethod, Class<?> containerClass) {
		return Arrays //
					  .stream(JqwikReflectionSupport.getMethodParameters(targetMethod, containerClass)) //
					  .filter(this::isForAllPresent) //
					  .collect(Collectors.toList());
	}

	private boolean isForAllPresent(MethodParameter parameter) {
		return parameter.isAnnotated(ForAll.class);
	}

}
