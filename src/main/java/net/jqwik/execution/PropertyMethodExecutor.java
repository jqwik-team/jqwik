package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import java.util.function.*;
import java.util.logging.*;

import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

public class PropertyMethodExecutor {

	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private final PropertyMethodDescriptor methodDescriptor;
	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public PropertyMethodExecutor(PropertyMethodDescriptor methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public TestExecutionResult execute(LifecycleSupplier lifecycleSupplier, EngineExecutionListener listener) {
		Object testInstance;
		try {
			testInstance = createTestInstance();
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.getContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return executePropertyMethod(testInstance, lifecycleSupplier, listener);
	}

	private Object createTestInstance() {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private TestExecutionResult executePropertyMethod(Object testInstance, LifecycleSupplier lifecycleSupplier, EngineExecutionListener listener) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		TeardownPropertyHook lifecycle = lifecycleSupplier.propertyFinallyLifecycle(methodDescriptor);
		PropertyLifecycleContext context = new DefaultPropertyLifecycleContext(methodDescriptor, testInstance);
		try {
			testExecutionResult = executeMethod(testInstance, listener);
		} finally {
			try {
				lifecycle.teardownProperty(context);
			} catch (Throwable throwable) {
				if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
					testExecutionResult = TestExecutionResult.failed(throwable);
				} else {
					LOG.warning(String.format("Exception occurred during teardown: %s", throwable));
				}
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
