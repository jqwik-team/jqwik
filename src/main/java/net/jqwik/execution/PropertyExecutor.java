package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutor {

	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public void execute(PropertyMethodDescriptor methodDescriptor, EngineExecutionListener listener, Function<Object, PropertyLifecycle> lifecycleSupplier) {
		if (hasUnspecifiedParameters(methodDescriptor)) {
			listener.executionSkipped(methodDescriptor, "Cannot run methods with unbound parameters - yet.");
			return;
		}
		listener.executionStarted(methodDescriptor);
		TestExecutionResult executionResult = executeTestMethod(methodDescriptor, lifecycleSupplier, listener);
		listener.executionFinished(methodDescriptor, executionResult);
	}

	private boolean hasUnspecifiedParameters(PropertyMethodDescriptor methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
			.anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private TestExecutionResult executeTestMethod(PropertyMethodDescriptor methodDescriptor, Function<Object, PropertyLifecycle> lifecycleSupplier,
												  EngineExecutionListener listener) {
		Object testInstance = null;
		try {
			testInstance = createTestInstance(methodDescriptor);
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
				methodDescriptor.getContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeTestMethod(methodDescriptor, testInstance, lifecycleSupplier, listener);
	}

	private Object createTestInstance(PropertyMethodDescriptor methodDescriptor) {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private TestExecutionResult invokeTestMethod(PropertyMethodDescriptor methodDescriptor, Object testInstance, Function<Object, PropertyLifecycle> lifecycleSupplier,
												 EngineExecutionListener listener) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = executeMethod(methodDescriptor, testInstance, listener);
		} finally {
			List<Throwable> throwableCollector = new ArrayList<>();
			lifecycleDoFinally(methodDescriptor, testInstance, lifecycleSupplier, throwableCollector);
			if (!throwableCollector.isEmpty() && testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
				// TODO: Use MultiException for reporting all exceptions
				testExecutionResult = TestExecutionResult.failed(throwableCollector.get(0));
			}
		}
		return testExecutionResult;
	}

	private TestExecutionResult executeMethod(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance,
											  EngineExecutionListener listener) {
		try {
			PropertyCheckResult propertyExecutionResult = executeProperty(propertyMethodDescriptor, testInstance);
			reportSeed(propertyMethodDescriptor, listener, propertyExecutionResult);
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

	private void lifecycleDoFinally(PropertyMethodDescriptor methodDescriptor, Object testInstance, Function<Object, PropertyLifecycle> lifecycleSupplier,
									List<Throwable> throwableCollector) {

		JqwikReflectionSupport.streamInnerInstances(testInstance).forEach(innerInstance -> {
			try {
				PropertyLifecycle lifecycle = lifecycleSupplier.apply(innerInstance);
				lifecycle.doFinally(methodDescriptor, innerInstance);
			} catch (Throwable throwable) {
				throwableCollector.add(throwable);
			}
		});
	}

	private TestExecutionResult createTestExecutionResult(PropertyCheckResult checkResult) {
		if (checkResult.status() == SATISFIED)
			return TestExecutionResult.successful();
		Throwable throwable = checkResult.throwable().orElse(new AssertionFailedError(checkResult.toString()));
		return TestExecutionResult.failed(throwable);
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

	private void reportSeed(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener, PropertyCheckResult checkResult) {
		if (checkResult.countTries() > 1 || checkResult.status() != SATISFIED)
			listener.reportingEntryPublished(propertyMethodDescriptor, createReportEntry(checkResult));
	}

	private ReportEntry createReportEntry(PropertyCheckResult checkResult) {
		Map<String, String> entries = new HashMap<>();
		entries.put("seed", Long.toString(checkResult.randomSeed()));
		entries.put("tries", Integer.toString(checkResult.countTries()));
		entries.put("checks", Integer.toString(checkResult.countChecks()));
		checkResult.sample().ifPresent( sample -> entries.put("sample", sample.toString()));
		checkResult.throwable().ifPresent( throwable -> entries.put("throwable", throwable.toString()));
		return ReportEntry.from(entries);
	}

	private PropertyCheckResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(propertyMethodDescriptor, testInstance);
		return property.check();
	}
}
