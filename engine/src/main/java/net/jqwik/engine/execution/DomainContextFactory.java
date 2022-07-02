package net.jqwik.engine.execution;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

class DomainContextFactory {

	private final PropertyLifecycleContext propertyLifecycleContext;
	private final PropertyMethodDescriptor methodDescriptor;

	DomainContextFactory(
		PropertyLifecycleContext propertyLifecycleContext,
		PropertyMethodDescriptor methodDescriptor
	) {
		this.propertyLifecycleContext = propertyLifecycleContext;
		this.methodDescriptor = methodDescriptor;
	}

	DomainContext createCombinedDomainContext() {
		Set<Domain> domainAnnotations = methodDescriptor.getDomains();

		if (domainAnnotations.isEmpty()) {
			return DomainContext.global();
		}
		Set<DomainContext> domainContexts =
			domainAnnotations
				.stream()
				.flatMap(this::expandDomain)
				.map(this::annotationToTuple)
				.distinct()
				.map(this::createDomainContext)
				.peek(domainContext -> {
					domainContext.initialize(propertyLifecycleContext);
				})
				.collect(CollectorsSupport.toLinkedHashSet());
		return new CombinedDomainContext(domainContexts);
	}

	// Use this transformation to make distinct() call on stream meaningful
	private Tuple.Tuple2<Class<? extends DomainContext>, Integer> annotationToTuple(Domain domain) {
		return Tuple.of(domain.value(), domain.priority());
	}

	private Stream<Domain> expandDomain(Domain domain) {
		Stream<Domain> inheritedDomains =
			JqwikAnnotationSupport.findContainerAnnotations(domain.value(), Domain.class).stream();
		return Stream.concat(
			Stream.of(domain),
			inheritedDomains
		);
	}

	private DomainContext createDomainContext(Tuple.Tuple2<Class<? extends DomainContext>, Integer> domainSpec) {
		Class<? extends DomainContext> domainContextClass = domainSpec.get1();
		int domainPriority = domainSpec.get2();
		try {
			DomainContext domainContext =
				JqwikReflectionSupport.newInstanceInTestContext(domainContextClass, propertyLifecycleContext.testInstance());

			if (domainPriority != Domain.PRIORITY_NOT_SET) {
				domainContext.setDefaultPriority(domainPriority);
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
}
