package net.jqwik.execution;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.ReportEntry;
import org.opentest4j.*;

import net.jqwik.JqwikException;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.properties.PropertyCheckResult;
import net.jqwik.support.JqwikReflectionSupport;

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
			Consumer<ReportEntry> reporter = (ReportEntry entry) -> listener.reportingEntryPublished(methodDescriptor, entry);
			PropertyCheckResult propertyExecutionResult = executeProperty(testInstance, reporter);
			TestExecutionResult testExecutionResult = createTestExecutionResult(propertyExecutionResult);
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

	private PropertyCheckResult executeProperty(Object testInstance, Consumer<ReportEntry> publisher) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(methodDescriptor, testInstance);
		return property.check(publisher);
	}

}
