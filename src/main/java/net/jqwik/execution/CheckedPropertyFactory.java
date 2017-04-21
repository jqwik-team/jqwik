package net.jqwik.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

public class CheckedPropertyFactory {

	private static List<Class<?>> BOOLEAN_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	public CheckedProperty fromDescriptor(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		String propertyName = propertyMethodDescriptor.getLabel();

		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		Property property = AnnotationSupport.findAnnotation(propertyMethod, Property.class).orElseThrow(() -> {
			String message = String.format("Method [%s] is not annotated with @Property", propertyMethod);
			return new JqwikException(message);
		});

		int tries = property.tries();
		long randomSeed = property.seed();

		CheckedFunction forAllFunction = createForAllFunction(propertyMethodDescriptor, testInstance);
		List<Parameter> forAllParameters = extractForAllParameters(propertyMethod);
		PropertyMethodArbitraryResolver arbitraryProvider = new PropertyMethodArbitraryResolver(propertyMethodDescriptor, testInstance);
		return new CheckedProperty(propertyName, forAllFunction, forAllParameters, arbitraryProvider, tries, randomSeed);
	}

	private CheckedFunction createForAllFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		// Todo: Bind all non @ForAll params first
		Class<?> returnType = propertyMethodDescriptor.getTargetMethod().getReturnType();
		Function<List, Object> function = params -> JqwikReflectionSupport.invokeMethod(propertyMethodDescriptor.getTargetMethod(),
				testInstance, params.toArray());
		if (BOOLEAN_RETURN_TYPES.contains(returnType))
			return params -> (boolean) function.apply(params);
		else
			return params -> {
				function.apply(params);
				return true;
			};
	}

	private List<Parameter> extractForAllParameters(Method targetMethod) {
		return Arrays //
				.stream(targetMethod.getParameters()) //
				.filter(parameter -> isForAllPresent(parameter)) //
				.collect(Collectors.toList());
	}

	private boolean isForAllPresent(Parameter parameter) {
		return AnnotationSupport.isAnnotated(parameter, ForAll.class);
	}

}
