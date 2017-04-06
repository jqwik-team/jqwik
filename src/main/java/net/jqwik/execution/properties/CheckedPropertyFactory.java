package net.jqwik.execution.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;
import org.junit.platform.commons.support.*;

public class CheckedPropertyFactory {

	public CheckedProperty fromDescriptor(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		String propertyName = propertyMethodDescriptor.getLabel();

		Property property = propertyMethodDescriptor.getTargetMethod().getDeclaredAnnotation(Property.class);
		int tries = property.tries();
		long randomSeed = property.seed();

		try {
			CheckedFunction assumeFunction = createAssumeFunction(propertyMethodDescriptor, testInstance);
			CheckedFunction forAllFunction = createForAllFunction(propertyMethodDescriptor, testInstance);
			List<Parameter> forAllParameters = extractForAllParameters(propertyMethodDescriptor.getTargetMethod());
			PropertyMethodArbitraryProvider arbitraryProvider = new PropertyMethodArbitraryProvider(propertyMethodDescriptor, testInstance);
			return new ExecutingCheckedProperty(propertyName, assumeFunction, forAllFunction, forAllParameters, arbitraryProvider, tries,
					randomSeed);
		} catch (CannotFindAssumptionException | AssumptionParametersDoNotMatchException assumptionException) {
			return new AbortingCheckedProperty(assumptionException, randomSeed);
		}
	}

	private CheckedFunction createAssumeFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		Method propertyMethod = propertyMethodDescriptor.getTargetMethod();
		if (!propertyMethod.isAnnotationPresent(Assume.class))
			return params -> true;

		String neededAssumptionName = propertyMethod.getDeclaredAnnotation(Assume.class).value();
		Predicate<Method> assumptionNameFits = method -> {
			if (!method.isAnnotationPresent(Assumption.class))
				return false;
			String assumptionName = method.getDeclaredAnnotation(Assumption.class).value();
			if (assumptionName.isEmpty())
				assumptionName = method.getName();
			return neededAssumptionName.equals(assumptionName);
		};
		List<Method> assumptionCandidates = ReflectionSupport.findMethods(propertyMethodDescriptor.getContainerClass(), assumptionNameFits,
				HierarchyTraversalMode.BOTTOM_UP);
		if (assumptionCandidates.isEmpty())
			throw new CannotFindAssumptionException(propertyMethod);

		Method assumptionMethod = assumptionCandidates.get(0);

		if (!allParametersMatch(assumptionMethod, propertyMethod)) {
			throw new AssumptionParametersDoNotMatchException(propertyMethod, assumptionMethod);
		}

		return params -> (boolean) JqwikReflectionSupport.invokeMethod(assumptionMethod, testInstance, params);
	}

	private boolean allParametersMatch(Method assumptionMethod, Method propertyMethod) {
		List<Parameter> assumptionParams = Arrays.asList(assumptionMethod.getParameters());
		List<Parameter> forAllParams = extractForAllParameters(propertyMethod);
		if (forAllParams.size() != assumptionParams.size())
			return false;
		for (int i = 0; i < assumptionParams.size(); i++) {
			GenericType assumptionType = new GenericType(assumptionParams.get(i).getType());
			GenericType forAllType = new GenericType(forAllParams.get(i).getType());
			if (!assumptionType.isAssignableFrom(forAllType) && ! forAllType.isAssignableFrom(assumptionType))
				return false;
		}
		return true;
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
