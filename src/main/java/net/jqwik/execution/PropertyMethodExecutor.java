package net.jqwik.execution;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

import static net.jqwik.properties.PropertyCheckResult.Status.*;

public class PropertyMethodExecutor {

	private static final Logger LOG = Logger.getLogger(PropertyMethodExecutor.class.getName());

	private final PropertyMethodDescriptor methodDescriptor;
	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public PropertyMethodExecutor(PropertyMethodDescriptor methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public PropertyExecutionResult execute(LifecycleSupplier lifecycleSupplier, PropertyExecutionListener listener) {
		Object testInstance;
		try {
			testInstance = createTestInstance();
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.getContainerClass());
			return PropertyExecutionResult.failed(new JqwikException(message, throwable));
		}
		return executePropertyMethod(testInstance, lifecycleSupplier, listener);
	}

	private Object createTestInstance() {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private PropertyExecutionResult executePropertyMethod(
		Object testInstance,
		LifecycleSupplier lifecycleSupplier,
		PropertyExecutionListener listener
	) {
		PropertyExecutionResult testExecutionResult = PropertyExecutionResult.successful();
		AroundPropertyHook around = lifecycleSupplier.aroundPropertyHook(methodDescriptor);
		PropertyLifecycleContext context = new DefaultPropertyLifecycleContext(methodDescriptor, testInstance);
		try {
			testExecutionResult = around.aroundProperty(context, () -> executeMethod(testInstance, listener));
		} catch (Throwable throwable) {
			if (testExecutionResult.getStatus() == Status.SUCCESSFUL) {
				return PropertyExecutionResult.failed(throwable);
			} else {
				LOG.warning(throwable.toString());
				return testExecutionResult;
			}
		}
		return testExecutionResult;
	}

	private PropertyExecutionResult executeMethod(Object testInstance, PropertyExecutionListener listener) {
		try {
			Consumer<ReportEntry> reporter = (ReportEntry entry) -> listener.reportingEntryPublished(methodDescriptor, entry);
			PropertyCheckResult propertyExecutionResult = executeProperty(testInstance, reporter);
			return createTestExecutionResult(propertyExecutionResult);
		} catch (TestAbortedException e) {
			return PropertyExecutionResult.aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return PropertyExecutionResult.failed(t);
		}
	}

	private PropertyExecutionResult createTestExecutionResult(PropertyCheckResult checkResult) {
		if (checkResult.status() == SATISFIED)
			return PropertyExecutionResult.successful();
		Throwable throwable = checkResult.throwable().orElse(new AssertionFailedError(checkResult.toString()));
		return PropertyExecutionResult.failed(throwable);
	}

	private PropertyCheckResult executeProperty(Object testInstance, Consumer<ReportEntry> publisher) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(methodDescriptor, testInstance);
		return property.check(publisher, methodDescriptor.getReporting());
	}

}
