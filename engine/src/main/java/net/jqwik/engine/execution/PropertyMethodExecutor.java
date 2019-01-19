package net.jqwik.engine.execution;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PropertyExecutionResult.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.properties.*;

import static org.junit.platform.commons.util.BlacklistedExceptions.*;

public class PropertyMethodExecutor {

	private static final Logger LOG = Logger.getLogger(PropertyMethodExecutor.class.getName());

	private final PropertyMethodDescriptor methodDescriptor;
	private final PropertyLifecycleContext propertyLifecycleContext;
	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public PropertyMethodExecutor(PropertyMethodDescriptor methodDescriptor, PropertyLifecycleContext propertyLifecycleContext) {
		this.methodDescriptor = methodDescriptor;
		this.propertyLifecycleContext = propertyLifecycleContext;
	}

	public PropertyExecutionResult execute(LifecycleSupplier lifecycleSupplier, PropertyExecutionListener listener) {
		try {
			DomainContext domainContext = combineDomainContexts(methodDescriptor.getDomainContexts());
			DomainContextFacadeImpl.currentContext.set(domainContext);
			return executePropertyMethod(lifecycleSupplier, listener);
		} finally {
			DomainContextFacadeImpl.currentContext.remove();
		}
	}

	private DomainContext combineDomainContexts(Set<Class<? extends DomainContext>> domainContextsClasses) {
		if (domainContextsClasses.isEmpty()) {
			return DomainContext.global();
		}
		Set<DomainContext> domainContexts =
			domainContextsClasses
				.stream()
				.map(this::createDomainContext)
				.collect(Collectors.toSet());
		return new CombinedDomainContext(domainContexts);
	}

	private DomainContext createDomainContext(Class<? extends DomainContext> domainContextClass) {
		try {
			DomainContext domainContext = ReflectionSupport.newInstance(domainContextClass);
			return domainContext;
		} catch (Throwable throwable) {
			String message = String.format(
				"Cannot instantiate domain context @Domain(\"%s\") on [%s].",
				domainContextClass, methodDescriptor.getTargetMethod()
			);
			throw new JqwikException(message);
		}
	}

	private PropertyExecutionResult executePropertyMethod(LifecycleSupplier lifecycleSupplier, PropertyExecutionListener listener) {
		PropertyExecutionResult propertyExecutionResult = PropertyExecutionResult.successful(methodDescriptor.getConfiguration().getSeed());
		AroundPropertyHook around = lifecycleSupplier.aroundPropertyHook(methodDescriptor);
		try {
			propertyExecutionResult = around.aroundProperty(
				propertyLifecycleContext,
				() -> executeMethod(propertyLifecycleContext.testInstance(), listener)
			);
		} catch (Throwable throwable) {
			if (propertyExecutionResult.getStatus() == Status.SUCCESSFUL) {
				return PropertyExecutionResult.failed(
					throwable,
					propertyExecutionResult.getSeed().orElse(null),
					propertyExecutionResult.getFalsifiedSample().orElse(null)
				);
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
