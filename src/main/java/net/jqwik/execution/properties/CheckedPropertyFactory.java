package net.jqwik.execution.properties;

import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Property;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckedPropertyFactory {

	public CheckedProperty fromDescriptor(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		String propertyName = propertyMethodDescriptor.getLabel();
		CheckedFunction forAllFunction = createForAllFunction(propertyMethodDescriptor, testInstance);
		List<Parameter> forAllParameters = extractForAllParameters(propertyMethodDescriptor.getTargetMethod());
		PropertyMethodArbitraryProvider arbitraryProvider = new PropertyMethodArbitraryProvider(propertyMethodDescriptor, testInstance);

		int tries = propertyMethodDescriptor.getTargetMethod().getDeclaredAnnotation(Property.class).tries();
		return new CheckedProperty(propertyName, forAllFunction, forAllParameters, arbitraryProvider, tries);
	}

	private CheckedFunction createForAllFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		// Todo: Bind all non @ForAll params first
		return params -> (boolean) JqwikReflectionSupport.invokeMethod(propertyMethodDescriptor.getTargetMethod(), testInstance, params);
	}

	private List<Parameter> extractForAllParameters(Method targetMethod) {
		return Arrays.stream(targetMethod.getParameters()).filter(parameter -> parameter.isAnnotationPresent(ForAll.class))
				.collect(Collectors.toList());
	}

}
