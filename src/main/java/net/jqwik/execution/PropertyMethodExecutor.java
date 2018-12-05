package net.jqwik.execution;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

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
			return PropertyExecutionResult.failed(new JqwikException(message, throwable), methodDescriptor.getConfiguration().getSeed(), null);
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
		PropertyExecutionResult propertyExecutionResult = PropertyExecutionResult.successful(methodDescriptor.getConfiguration().getSeed());
		AroundPropertyHook around = lifecycleSupplier.aroundPropertyHook(methodDescriptor);
		PropertyLifecycleContext context = new DefaultPropertyLifecycleContext(methodDescriptor, testInstance);
		try {
			propertyExecutionResult = around.aroundProperty(context, () -> executeMethod(testInstance, listener));
		} catch (Throwable throwable) {
			if (propertyExecutionResult.getStatus() == Status.SUCCESSFUL) {
				return PropertyExecutionResult.failed(
					throwable,
					propertyExecutionResult.getSeed().orElse(null),
					propertyExecutionResult.getFalsifiedSample().orElse(null));
			} else {
				LOG.warning(throwable.toString());
				return propertyExecutionResult;
			}
		}
		return propertyExecutionResult;
	}

	private PropertyExecutionResult executeMethod(Object testInstance, PropertyExecutionListener listener) {
		try {
			Consumer<ReportEntry> reporter = (ReportEntry entry) -> listener.reportingEntryPublished(methodDescriptor, entry);
			PropertyCheckResult checkResult = executeProperty(testInstance, reporter);
			return checkResult.toExecutionResult();
		} catch (TestAbortedException e) {
			return PropertyExecutionResult.aborted(e, methodDescriptor.getConfiguration().getSeed());
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return PropertyExecutionResult.failed(t, methodDescriptor.getConfiguration().getSeed(), null);
		}
	}

	private PropertyCheckResult executeProperty(Object testInstance, Consumer<ReportEntry> publisher) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(methodDescriptor, testInstance);
		return property.check(publisher, methodDescriptor.getReporting());
	}

}
