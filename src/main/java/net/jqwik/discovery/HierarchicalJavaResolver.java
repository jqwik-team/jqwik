package net.jqwik.discovery;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.support.ReflectionSupport.findMethods;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.discovery.predicates.IsContainerAGroup;
import net.jqwik.discovery.predicates.IsTestContainer;
import net.jqwik.support.JqwikReflectionSupport;

class HierarchicalJavaResolver {

	private static final Logger LOG = Logger.getLogger(HierarchicalJavaResolver.class.getName());

	private final TestDescriptor engineDescriptor;
	private final Set<ElementResolver> resolvers;

	private final IsContainerAGroup isContainerAGroup = new IsContainerAGroup();

	HierarchicalJavaResolver(TestDescriptor engineDescriptor, Set<ElementResolver> resolvers) {
		this.engineDescriptor = engineDescriptor;
		this.resolvers = resolvers;
	}

	void resolveClass(Class<?> testClass) {
		Set<TestDescriptor> resolvedDescriptors = resolveContainerWithParents(testClass);
		resolvedDescriptors.forEach(this::resolveChildren);

		if (resolvedDescriptors.isEmpty()) {
			LOG.warning(() -> format("Class '%s' could not be resolved", testClass.getName()));
		}
	}

	void resolveMethod(Class<?> testClass, Method testMethod) {
		Set<TestDescriptor> potentialParents = resolveContainerWithParents(testClass);
		Set<TestDescriptor> resolvedDescriptors = resolveForAllParents(testMethod, potentialParents);

		if (resolvedDescriptors.isEmpty()) {
			LOG.warning(() -> format("Method '%s' could not be resolved", testMethod.toGenericString()));
		}
	}

	private Set<TestDescriptor> resolveContainerWithParents(Class<?> testClass) {
		Set<TestDescriptor> potentialParents = isContainerAGroup.test(testClass) ?
				resolveContainerWithParents(testClass.getDeclaringClass()) :
				Collections.singleton(engineDescriptor);
		return resolveForAllParents(testClass, potentialParents);
	}

	void resolveUniqueId(UniqueId uniqueId) {
		List<UniqueId.Segment> segments = uniqueId.getSegments();
		segments.remove(0); // Ignore engine unique ID

		if (!resolveUniqueId(this.engineDescriptor, segments)) {
			LOG.warning(() -> format("Unique ID '%s' could not be resolved", uniqueId));
		}
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
		Set<TestDescriptor> resolvedDescriptors = new HashSet<>();
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
		List<Class<?>> containedContainersCandidates = JqwikReflectionSupport.findNestedClasses(containerClass, isGroup);
		containedContainersCandidates.forEach(nestedClass -> resolveContainerWithChildren(nestedClass, Collections.singleton(containerClassDescriptor)));
	}

	private void resolveContainedMethods(TestDescriptor containerDescriptor, Class<?> testClass) {
		List<Method> testMethodCandidates = findMethods(testClass, method -> !JqwikReflectionSupport.isPrivate(method), TOP_DOWN);
		testMethodCandidates.forEach(method -> resolve(method, containerDescriptor));
	}

	private Set<TestDescriptor> resolve(AnnotatedElement element, TestDescriptor parent) {
		return this.resolvers.stream() //
				.map(resolver -> tryToResolveWithResolver(element, parent, resolver)) //
				.filter(testDescriptors -> !testDescriptors.isEmpty()) //
				.flatMap(Collection::stream) //
				.collect(toSet());
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
