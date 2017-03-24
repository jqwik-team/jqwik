package net.jqwik.discovery;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.specs.*;
import net.jqwik.support.*;

public abstract class AbstractClassResolver implements ElementResolver {
	@Override
	public Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent) {
		if (!(element instanceof Class))
			return Collections.emptySet();

		Class<?> clazz = (Class<?>) element;
		if (!shouldBeResolved(clazz))
			return Collections.emptySet();

		UniqueId uniqueId = createUniqueId(clazz, parent);
		return Collections.singleton(resolveClass(clazz, uniqueId));
	}

	@Override
	public Optional<TestDescriptor> resolveUniqueId(UniqueId.Segment segment, TestDescriptor parent) {

		if (!segment.getType().equals(JqwikUniqueIDs.CONTAINER_SEGMENT_TYPE))
			return Optional.empty();

		if (!requiredParentType().isInstance(parent))
			return Optional.empty();

		String className = segment.getValue();

		Optional<Class<?>> optionalContainerClass = JqwikReflectionSupport.loadClass(className);
		if (!optionalContainerClass.isPresent())
			return Optional.empty();

		Class<?> containerClass = optionalContainerClass.get();
		if (!shouldBeResolved(containerClass))
			return Optional.empty();

		UniqueId uniqueId = createUniqueId(containerClass, parent);
		return Optional.of(resolveClass(containerClass, uniqueId));
	}

	private boolean shouldBeResolved(Class<?> element) {
		return getDiscoverySpec().shouldBeDiscovered(element);
	}

	private TestDescriptor resolveClass(Class<?> testClass, UniqueId uniqueId) {
		ContainerClassDescriptor newContainerDescriptor = createContainerDescriptor(testClass, uniqueId);
		if (getDiscoverySpec().butSkippedOnExecution(testClass)) {
			return new SkipExecutionDecorator(newContainerDescriptor, getDiscoverySpec().skippingReason(testClass));
		}
		return newContainerDescriptor;
	}

	protected abstract Class<? extends TestDescriptor> requiredParentType();

	protected abstract DiscoverySpec<Class<?>> getDiscoverySpec();

	protected abstract UniqueId createUniqueId(Class<?> testClass, TestDescriptor parent);

	protected abstract ContainerClassDescriptor createContainerDescriptor(Class<?> containerClass, UniqueId uniqueId);
}
