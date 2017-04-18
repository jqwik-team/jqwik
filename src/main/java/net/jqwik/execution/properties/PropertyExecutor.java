package net.jqwik.execution.properties;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
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
import net.jqwik.properties.*;

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
			PropertyCheckResult propertyExecutionResult = executeProperty(propertyMethodDescriptor, testInstance);
			reportSeed(propertyMethodDescriptor, listener, propertyExecutionResult.randomSeed());
			TestExecutionResult testExecutionResult = createTestExecutionResult(propertyExecutionResult);
			// Disabled until JUnit5 gets along with descriptors being both container and test
			// if (testExecutionResult.getStatus() == Status.FAILED)
			// addFailedPropertyChild(propertyMethodDescriptor, listener, seed);
			return testExecutionResult;
		} catch (TestAbortedException e) {
			return aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

	private TestExecutionResult createTestExecutionResult(PropertyCheckResult checkResult) {
		if (checkResult.status() == SATISFIED)
			return TestExecutionResult.successful();
		if (checkResult.status() == FALSIFIED)
			return TestExecutionResult.failed(new AssertionFailedError(checkResult.toString()));
		return TestExecutionResult.failed(checkResult.throwable().orElse(new AssertionFailedError(checkResult.toString())));
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

	private PropertyCheckResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(propertyMethodDescriptor, testInstance);
		return property.check();
	}
}
