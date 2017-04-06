package net.jqwik.execution.properties;

import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.support.*;

public class PropertyExecutor extends AbstractMethodExecutor<PropertyMethodDescriptor, PropertyLifecycle> {

	private static List<Class<?>> COMPATIBLE_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	@Override
	protected TestExecutionResult executeMethod(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance,
			EngineExecutionListener listener) {
		try {
			if (hasIncompatibleReturnType(propertyMethodDescriptor.getTargetMethod())) {
				String errorMessage = String.format("Property method [%s] must return boolean value",
						propertyMethodDescriptor.getTargetMethod());
				return aborted(new JqwikException(errorMessage));
			}
			if (hasForAllParameters(propertyMethodDescriptor.getTargetMethod())) {
				PropertyExecutionResult propertyExecutionResult = executeProperty(propertyMethodDescriptor, testInstance);
				long seed = propertyExecutionResult.getSeed();
				reportSeed(propertyMethodDescriptor, listener, seed);
				TestExecutionResult testExecutionResult = propertyExecutionResult.getTestExecutionResult();
				// Disabled until JUnit5 gets along with descriptors being both container and test
				// if (testExecutionResult.getStatus() == Status.FAILED)
				// addFailedPropertyChild(propertyMethodDescriptor, listener, seed);
				return testExecutionResult;
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

	private void addFailedPropertyChild(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener, long seed) {
		UniqueId childId = JqwikUniqueIDs.appendSeed(propertyMethodDescriptor.getUniqueId(), seed);
		SeededFailedPropertyDescriptor failedPropertyDescriptor = new SeededFailedPropertyDescriptor(childId, seed);
		propertyMethodDescriptor.addChild(failedPropertyDescriptor);

		listener.dynamicTestRegistered(failedPropertyDescriptor);
		listener.executionStarted(failedPropertyDescriptor);
		String message = String.format("Property [%s] failed with seed [%s]", propertyMethodDescriptor.getLabel(), seed);
		listener.executionFinished(failedPropertyDescriptor, TestExecutionResult.failed(new AssertionFailedError(message)));
	}

	private void reportSeed(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener, long seed) {
		listener.reportingEntryPublished(propertyMethodDescriptor, ReportEntry.from("seed", Long.toString(seed)));
	}

	private boolean hasIncompatibleReturnType(Method targetMethod) {
		return !COMPATIBLE_RETURN_TYPES.contains(targetMethod.getReturnType());
	}

	private PropertyExecutionResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(propertyMethodDescriptor, testInstance);
		return property.check();
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
		return Arrays.stream(targetMethod.getParameters()).anyMatch(parameter -> parameter.isAnnotationPresent(ForAll.class));
	}

}
