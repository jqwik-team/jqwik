package net.jqwik.engine.execution;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class PropertyMethodExecutor {

	private static final Logger LOG = Logger.getLogger(PropertyMethodExecutor.class.getName());

	private final PropertyMethodDescriptor methodDescriptor;
	private final PropertyLifecycleContext propertyLifecycleContext;
	private final boolean reportOnlyFailures;
	private final CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	public PropertyMethodExecutor(
		PropertyMethodDescriptor methodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		boolean reportOnlyFailures
	) {
		this.methodDescriptor = methodDescriptor;
		this.propertyLifecycleContext = propertyLifecycleContext;
		this.reportOnlyFailures = reportOnlyFailures;
	}

	public PropertyExecutionResult execute(LifecycleHooksSupplier lifecycleSupplier, PropertyExecutionListener listener) {
		try {
			DomainContext domainContext = combineDomainContexts(methodDescriptor.getDomains());
			DomainContextFacadeImpl.setCurrentContext(domainContext);
			return executePropertyMethod(lifecycleSupplier, listener);
		} finally {
			DomainContextFacadeImpl.removeCurrentContext();
		}
	}

	private DomainContext combineDomainContexts(Set<Domain> domainAnnotations) {
		if (domainAnnotations.isEmpty()) {
			return DomainContext.global();
		}
		Set<DomainContext> domainContexts =
			domainAnnotations
				.stream()
				.map(this::createDomainContext)
				.collect(Collectors.toSet());
		return new CombinedDomainContext(domainContexts);
	}

	private DomainContext createDomainContext(Domain domain) {
		Class<? extends DomainContext> domainContextClass = domain.value();
		try {
			DomainContext domainContext =
				JqwikReflectionSupport.newInstanceInTestContext(domainContextClass, propertyLifecycleContext.testInstance());

			if (domain.priority() != Domain.PRIORITY_NOT_SET) {
				domainContext.setDefaultPriority(domain.priority());
			}
			return domainContext;
		} catch (Throwable throwable) {
			String message = String.format(
				"Cannot instantiate domain context @Domain(\"%s\") on [%s].",
				domainContextClass, methodDescriptor.getTargetMethod()
			);
			throw new JqwikException(message);
		}
	}

	private PropertyExecutionResult executePropertyMethod(LifecycleHooksSupplier lifecycleSupplier, PropertyExecutionListener listener) {
		Consumer<ReportEntry> publisher = (ReportEntry entry) -> listener.reportingEntryPublished(methodDescriptor, entry);
		AroundPropertyHook aroundProperty = lifecycleSupplier.aroundPropertyHook(methodDescriptor);
		AroundTryHook aroundTry = lifecycleSupplier.aroundTryHook(methodDescriptor);
		ResolveParameterHook resolveParameter = lifecycleSupplier.resolveParameterHook(methodDescriptor);

		PropertyExecutionResult propertyExecutionResult;
		try {
			propertyExecutionResult = aroundProperty.aroundProperty(
				propertyLifecycleContext,
				() -> {
					return executeMethod(publisher, aroundTry, resolveParameter);
				}
			);
		} catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
			propertyExecutionResult = PlainExecutionResult.failed(
				throwable,
				methodDescriptor.getConfiguration().getSeed()
			);
		}
		StoreRepository.getCurrent().finishProperty(methodDescriptor);
		StoreRepository.getCurrent().finishScope(methodDescriptor);
		reportResult(publisher, propertyExecutionResult);
		return propertyExecutionResult;
	}

	private ExtendedPropertyExecutionResult executeMethod(
		Consumer<ReportEntry> publisher,
		AroundTryHook aroundTry,
		ResolveParameterHook resolveParameter
	) {
		try {
			return executeProperty(publisher, aroundTry, resolveParameter);
		} catch (TestAbortedException e) {
			return PlainExecutionResult.aborted(e, methodDescriptor.getConfiguration().getSeed());
		} catch (Throwable t) {
			JqwikExceptionSupport.rethrowIfBlacklisted(t);
			return PlainExecutionResult.failed(t, methodDescriptor.getConfiguration().getSeed());
		}
	}

	private PropertyCheckResult executeProperty(
		Consumer<ReportEntry> publisher,
		AroundTryHook aroundTry,
		ResolveParameterHook resolveParameter
	) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(
			methodDescriptor,
			propertyLifecycleContext,
			aroundTry,
			resolveParameter
		);
		return property.check(publisher, methodDescriptor.getReporting());
	}

	private void reportResult(Consumer<ReportEntry> publisher, PropertyExecutionResult executionResult) {
		if (executionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL && reportOnlyFailures) {
			return;
		}

		if (executionResult instanceof ExtendedPropertyExecutionResult) {
			if (isReportWorthy((ExtendedPropertyExecutionResult) executionResult)) {
				ReportEntry reportEntry = ExecutionResultReportEntry.from(
					methodDescriptor,
					(ExtendedPropertyExecutionResult) executionResult
				);
				publisher.accept(reportEntry);
			}
		} else {
			String message = String.format("Unknown PropertyExecutionResult implementation: %s", executionResult.getClass());
			LOG.warning(message);
		}
	}

	private boolean isReportWorthy(ExtendedPropertyExecutionResult executionResult) {
		if (executionResult.status() != PropertyExecutionResult.Status.SUCCESSFUL) {
			return true;
		}
		return executionResult.countTries() > 1;
	}

}
