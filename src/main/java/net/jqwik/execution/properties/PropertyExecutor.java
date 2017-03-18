package net.jqwik.execution.properties;

import static org.junit.platform.commons.util.BlacklistedExceptions.rethrowIfBlacklisted;
import static org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import net.jqwik.JqwikException;
import net.jqwik.api.properties.ForAll;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.execution.AbstractMethodExecutor;
import net.jqwik.support.JqwikReflectionSupport;

public class PropertyExecutor extends AbstractMethodExecutor {

	private static List<Class<?>> COMPATIBLE_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	@Override
	protected TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance) {
		try {
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) methodDescriptor;
			if (hasIncompatibleReturnType(propertyMethodDescriptor.getTargetMethod())) {
				String errorMessage = String.format("Property method [%s] must return boolean value",
						propertyMethodDescriptor.getTargetMethod());
				return aborted(new JqwikException(errorMessage));
			}
			if (hasForAllParameters(methodDescriptor.getTargetMethod())) {
				return executeProperty(propertyMethodDescriptor, testInstance);
			} else {
				return executePropertyWithoutForAllParameters(propertyMethodDescriptor, testInstance);
			}
		} catch (TestAbortedException e) {
			return aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

	private boolean hasIncompatibleReturnType(Method targetMethod) {
		return !COMPATIBLE_RETURN_TYPES.contains(targetMethod.getReturnType());
	}

	private TestExecutionResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {

		String propertyName = propertyMethodDescriptor.getLabel();
		CheckedFunction forAllFunction = createForAllFunction(propertyMethodDescriptor, testInstance);
		List<Parameter> forAllParameters = extractForAllParameters(propertyMethodDescriptor.getTargetMethod());
		PropertyMethodArbitraryProvider arbitraryProvider = new PropertyMethodArbitraryProvider(propertyMethodDescriptor, testInstance);

		CheckedProperty property = new CheckedProperty(propertyName, forAllFunction, forAllParameters, arbitraryProvider);
		return property.check();
	}

	private CheckedFunction createForAllFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		// Todo: Bind all non @ForAll params first
		return params -> (boolean) JqwikReflectionSupport.invokeMethod(propertyMethodDescriptor.getTargetMethod(), testInstance, params);
	}

	private TestExecutionResult executePropertyWithoutForAllParameters(PropertyMethodDescriptor methodDescriptor, Object testInstance) {
		boolean success = (boolean) JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);

		if (success)
			return successful();
		else {
			String propertyFailedMessage = String.format("Property [%s] failed", methodDescriptor.getLabel());
			return failed(new AssertionFailedError(propertyFailedMessage));
		}
	}

	private boolean hasForAllParameters(Method targetMethod) {
		return extractForAllParameters(targetMethod).size() > 0;
	}

	private List<Parameter> extractForAllParameters(Method targetMethod) {
		return Arrays.stream(targetMethod.getParameters()).filter(parameter -> parameter.isAnnotationPresent(ForAll.class))
				.collect(Collectors.toList());
	}

}
