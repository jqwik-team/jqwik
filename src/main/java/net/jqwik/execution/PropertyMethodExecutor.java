package net.jqwik.execution;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

public class PropertyMethodExecutor {

	private final PropertyMethodDescriptor methodDescriptor;
	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public PropertyMethodExecutor(PropertyMethodDescriptor methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public TestExecutionResult execute(Function<Object, PropertyLifecycle> lifecycleSupplier, EngineExecutionListener listener) {
		Object testInstance = null;
		try {
			testInstance = createTestInstance();
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.getContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeTestMethod(testInstance, lifecycleSupplier, listener);
	}

	private Object createTestInstance() {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private TestExecutionResult invokeTestMethod(Object testInstance, Function<Object, PropertyLifecycle> lifecycleSupplier,
			EngineExecutionListener listener) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = executeMethod(testInstance, listener);
		} finally {
			List<Throwable> throwableCollector = new ArrayList<>();
			lifecycleDoFinally(testInstance, lifecycleSupplier, throwableCollector);
			if (!throwableCollector.isEmpty() && testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
				// TODO: Use MultiException for reporting all exceptions
				testExecutionResult = TestExecutionResult.failed(throwableCollector.get(0));
			}
		}
		return testExecutionResult;
	}

	private TestExecutionResult executeMethod(Object testInstance, EngineExecutionListener listener) {
		try {
			PropertyCheckResult propertyExecutionResult = executeProperty(testInstance);
			reportSeed(listener, propertyExecutionResult);
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

	private void lifecycleDoFinally(Object testInstance, Function<Object, PropertyLifecycle> lifecycleSupplier,
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

	private void addFailedPropertyChild(EngineExecutionListener listener, long seed) {
		UniqueId childId = JqwikUniqueIDs.appendSeed(methodDescriptor.getUniqueId(), seed);
		SeededFailedPropertyDescriptor failedPropertyDescriptor = new SeededFailedPropertyDescriptor(childId, seed);
		methodDescriptor.addChild(failedPropertyDescriptor);

		listener.dynamicTestRegistered(failedPropertyDescriptor);
		listener.executionStarted(failedPropertyDescriptor);
		String message = String.format("Property [%s] failed with seed [%s]", methodDescriptor.getLabel(), seed);
		listener.executionFinished(failedPropertyDescriptor, TestExecutionResult.failed(new AssertionFailedError(message)));
	}

	private void reportSeed(EngineExecutionListener listener, PropertyCheckResult checkResult) {
		if (checkResult.countTries() > 1 || checkResult.status() != SATISFIED)
			listener.reportingEntryPublished(methodDescriptor, createReportEntry(checkResult));
	}

	private ReportEntry createReportEntry(PropertyCheckResult checkResult) {
		Map<String, String> entries = new HashMap<>();
		entries.put("seed", Long.toString(checkResult.randomSeed()));
		entries.put("tries", Integer.toString(checkResult.countTries()));
		entries.put("checks", Integer.toString(checkResult.countChecks()));
		checkResult.sample().ifPresent(sample -> entries.put("sample", sample.toString()));
		checkResult.throwable().ifPresent(throwable -> entries.put("throwable", throwable.toString()));
		return ReportEntry.from(entries);
	}

	private PropertyCheckResult executeProperty(Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(methodDescriptor, testInstance);
		return property.check();
	}

}
