package net.jqwik.engine.discovery;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.support.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.predicates.*;
import net.jqwik.engine.support.*;

import static java.lang.String.*;
import static org.junit.platform.commons.support.HierarchyTraversalMode.*;
import static org.junit.platform.commons.support.ReflectionSupport.*;
import static org.junit.platform.engine.SelectorResolutionResult.*;

class HierarchicalJavaResolver {

	private static final Logger LOG = Logger.getLogger(HierarchicalJavaResolver.class.getName());

	private final static IsContainerAGroup isContainerAGroup = new IsContainerAGroup();
	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();

	private final TestDescriptor engineDescriptor;
	private final Set<ElementResolver> resolvers;

	HierarchicalJavaResolver(TestDescriptor engineDescriptor, Set<ElementResolver> resolvers) {
		this.engineDescriptor = engineDescriptor;
		this.resolvers = resolvers;
	}

	SelectorResolutionResult resolveClass(Class<?> testClass) {
		return resolveSafely(() -> {
			Set<TestDescriptor> resolvedDescriptors = resolveContainerWithParents(testClass);
			resolvedDescriptors.forEach(this::resolveChildren);
			warnWhenJUnitAnnotationsArePresent(resolvedDescriptors);

			if (resolvedDescriptors.isEmpty()) {
				return SelectorResolutionResult.unresolved();
			}

			return SelectorResolutionResult.resolved();
		});
	}

	private void warnWhenJUnitAnnotationsArePresent(Set<TestDescriptor> resolvedDescriptors) {
		resolvedDescriptors
			.stream()
			.filter(d -> !d.getChildren().isEmpty())
			.filter(d -> d instanceof ContainerClassDescriptor)
			.map(d -> (ContainerClassDescriptor) d)
			.forEach(d -> DiscoverySupport.warnWhenJunitAnnotationsArePresent(d.getContainerClass()));
	}

	SelectorResolutionResult resolveMethod(Class<?> testClass, Method testMethod) {
		return resolveSafely(() -> {
			Set<TestDescriptor> potentialParents = resolveContainerWithParents(testClass);
			Set<TestDescriptor> resolvedDescriptors = resolveForAllParents(testMethod, potentialParents);

			if (resolvedDescriptors.isEmpty()) {
				return SelectorResolutionResult.unresolved();
			}
			return SelectorResolutionResult.resolved();
		});
	}

	SelectorResolutionResult resolveUniqueId(UniqueId uniqueId) {
		return resolveSafely(() -> {
			// Silently ignore request to resolve foreign ID
			if (uniqueId.getEngineId().isPresent()) {
				if (!uniqueId.getEngineId().get().equals(JqwikTestEngine.ENGINE_ID)) {
					return SelectorResolutionResult.unresolved();
				}
			}

			List<UniqueId.Segment> segments = new ArrayList<>(uniqueId.getSegments());
			segments.remove(0); // Ignore engine unique ID

			if (!resolveUniqueId(this.engineDescriptor, segments)) {
				// This is more severe than unresolvable methods or classes because only suitable IDs should get here anyway
				LOG.warning(() -> format("Received request to resolve unique id '%s' as test or test container but could not fulfill it", uniqueId));
				return SelectorResolutionResult.unresolved();
			}
			return SelectorResolutionResult.resolved();
		});
	}

	private SelectorResolutionResult resolveSafely(Callable<SelectorResolutionResult> callable) {
		try {
			return callable.call();
		} catch (Throwable t) {
			JqwikExceptionSupport.rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

	private Set<TestDescriptor> resolveContainerWithParents(Class<?> testClass) {
		Set<TestDescriptor> potentialParents =
			isContainerAGroup.test(testClass)
				? resolveContainerWithParents(testClass.getDeclaringClass())
				: Collections.singleton(engineDescriptor);
		return resolveForAllParents(testClass, potentialParents);
	}

	/**
	 * Return true if all segments of unique ID could be resolved
	 */
	private boolean resolveUniqueId(TestDescriptor parent, List<UniqueId.Segment> remainingSegments) {
		if (remainingSegments.isEmpty()) {
			resolveChildren(parent);
			return true;
		}

		UniqueId.Segment head = remainingSegments.remove(0);
		for (ElementResolver resolver : resolvers) {
			Optional<TestDescriptor> resolvedDescriptor = resolver.resolveUniqueId(head, parent);
			if (!resolvedDescriptor.isPresent())
				continue;

			Optional<TestDescriptor> foundTestDescriptor = findTestDescriptorByUniqueId(resolvedDescriptor.get().getUniqueId());
			TestDescriptor descriptor = foundTestDescriptor.orElseGet(() -> {
				TestDescriptor newDescriptor = resolvedDescriptor.get();
				parent.addChild(newDescriptor);
				return newDescriptor;
			});
			return resolveUniqueId(descriptor, remainingSegments);
		}
		return false;
	}

	private Set<TestDescriptor> resolveContainerWithChildren(Class<?> containerClass, Set<TestDescriptor> potentialParents) {
		Set<TestDescriptor> resolvedDescriptors = resolveForAllParents(containerClass, potentialParents);
		resolvedDescriptors.forEach(this::resolveChildren);
		return resolvedDescriptors;
	}

	private Set<TestDescriptor> resolveForAllParents(AnnotatedElement element, Set<TestDescriptor> potentialParents) {
		Set<TestDescriptor> resolvedDescriptors = new LinkedHashSet<>();
		potentialParents.forEach(parent -> resolvedDescriptors.addAll(resolve(element, parent)));
		return resolvedDescriptors;
	}

	private void resolveChildren(TestDescriptor descriptor) {
		if (descriptor instanceof ContainerClassDescriptor) {
			ContainerClassDescriptor containerClassDescriptor = (ContainerClassDescriptor) descriptor;
			Class<?> containerClass = containerClassDescriptor.getContainerClass();
			resolveContainedMethods(descriptor, containerClass);

			resolveContainedGroups(containerClassDescriptor, containerClass);
		}
	}

	private void resolveContainedGroups(ContainerClassDescriptor containerClassDescriptor, Class<?> containerClass) {
		Predicate<Class<?>> isGroup = new IsContainerAGroup();
		List<Class<?>> containedContainersCandidates = findNestedClasses(containerClass, isGroup);
		containedContainersCandidates
			.forEach(nestedClass -> resolveContainerWithChildren(nestedClass, Collections.singleton(containerClassDescriptor)));
	}

	private void resolveContainedMethods(TestDescriptor containerDescriptor, Class<?> testClass) {
		List<Method> testMethodCandidates = findMethods(testClass, isDiscoverableTestMethod, TOP_DOWN);
		testMethodCandidates.forEach(method -> resolve(method, containerDescriptor));
	}

	private Set<TestDescriptor> resolve(AnnotatedElement element, TestDescriptor parent) {
		return this.resolvers
				   .stream()
				   .map(resolver -> tryToResolveWithResolver(element, parent, resolver))
				   .filter(testDescriptors -> !testDescriptors.isEmpty())
				   .flatMap(Collection::stream)
				   .collect(CollectorsSupport.toLinkedHashSet());
	}

	private Set<TestDescriptor> tryToResolveWithResolver(AnnotatedElement element, TestDescriptor parent, ElementResolver resolver) {

		Set<TestDescriptor> resolvedDescriptors = resolver.resolveElement(element, parent);
		Set<TestDescriptor> result = new LinkedHashSet<>();

		resolvedDescriptors.forEach(testDescriptor -> {
			Optional<TestDescriptor> existingTestDescriptor = findTestDescriptorByUniqueId(testDescriptor.getUniqueId());
			if (existingTestDescriptor.isPresent()) {
				result.add(existingTestDescriptor.get());
			} else {
				parent.addChild(testDescriptor);
				result.add(testDescriptor);
			}
		});

		return result;
	}

	@SuppressWarnings("unchecked")
	private Optional<TestDescriptor> findTestDescriptorByUniqueId(UniqueId uniqueId) {
		return (Optional<TestDescriptor>) this.engineDescriptor.findByUniqueId(uniqueId);
	}

}
