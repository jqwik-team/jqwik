package net.jqwik.execution.properties;

import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.properties.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutor extends AbstractMethodExecutor<PropertyMethodDescriptor, PropertyLifecycle> {

	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	@Override
	protected TestExecutionResult executeMethod(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance,
			EngineExecutionListener listener) {
		try {
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

	private void reportSeed(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener, long seed) {
		listener.reportingEntryPublished(propertyMethodDescriptor, ReportEntry.from("seed", Long.toString(seed)));
	}

	private PropertyCheckResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(propertyMethodDescriptor, testInstance);
		return property.check();
	}
}
