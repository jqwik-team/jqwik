package net.jqwik.execution.properties;

import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class CheckedPropertyFactory {

	public CheckedProperty fromDescriptor(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		String propertyName = propertyMethodDescriptor.getLabel();

		Property property = propertyMethodDescriptor.getTargetMethod().getDeclaredAnnotation(Property.class);
		int tries = property.tries();
		long randomSeed = property.seed();

		CheckedFunction assumeFunction = createAssumeFunction(propertyMethodDescriptor, testInstance);
		if (assumeFunction == null) {
			return new FailingCheckedProperty(new CannotFindAssumptionException(propertyMethodDescriptor.getTargetMethod()), randomSeed);
		}

		CheckedFunction forAllFunction = createForAllFunction(propertyMethodDescriptor, testInstance);
		List<Parameter> forAllParameters = extractForAllParameters(propertyMethodDescriptor.getTargetMethod());
		PropertyMethodArbitraryProvider arbitraryProvider = new PropertyMethodArbitraryProvider(propertyMethodDescriptor, testInstance);

		return new DefaultCheckedProperty(propertyName, assumeFunction, forAllFunction, forAllParameters, arbitraryProvider, tries, randomSeed);
	}

	private CheckedFunction createAssumeFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		if (propertyMethodDescriptor.getTargetMethod().isAnnotationPresent(Assume.class))
			return null;

		// Todo: Use @Assume annotation to retrieve method and convert it to CheckedFunction
		return params -> true;
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
