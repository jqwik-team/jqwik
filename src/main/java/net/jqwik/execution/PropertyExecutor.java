package net.jqwik.execution;

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

import javaslang.CheckedFunction1;
import javaslang.test.*;
import net.jqwik.JqwikException;
import net.jqwik.api.ForAll;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

public class PropertyExecutor extends AbstractMethodExecutor {

	@Override
	protected TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance) {
		try {
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) methodDescriptor;
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

	private TestExecutionResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckResult result = createJavaSlangProperty(propertyMethodDescriptor, testInstance).check();

		if (result.isSatisfied())
			return successful();
		else {
			String propertyFailedMessage = String.format("Property [%s] failed: %s", propertyMethodDescriptor.getLabel(),
					result.toString());
			return failed(new AssertionFailedError(propertyFailedMessage));
		}
	}

	private Checkable createJavaSlangProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		Class<?> paramType1 = int.class;

		Arbitrary<Object> arbitrary = new GenericWrapper(Arbitrary.integer());
		CheckedFunction1<Object, Boolean> function1 = createCheckedFunction(propertyMethodDescriptor, testInstance);
		return Property.def(propertyMethodDescriptor.getLabel()).forAll(arbitrary).suchThat(function1);
	}

	private CheckedFunction1<Object, Boolean> createCheckedFunction(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		return o -> (boolean) JqwikReflectionSupport
				.invokeMethod(propertyMethodDescriptor.getTargetMethod(), testInstance, o);
	}

	private TestExecutionResult executePropertyWithoutForAllParameters(PropertyMethodDescriptor methodDescriptor, Object testInstance) {
		Object result = JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);
		if (!result.getClass().equals(Boolean.class)) {
			throw new JqwikException(String.format("Property method [%s] must return boolean value", methodDescriptor.getTargetMethod()));
		}
		boolean success = (boolean) result;

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

	private static class GenericWrapper implements Arbitrary<Object> {

		private final Arbitrary<?> wrapped;

		GenericWrapper(Arbitrary<?> wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Gen<Object> apply(int size) {
			return (Gen<Object>) wrapped.apply(size);
		}
	}
}
