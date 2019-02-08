package net.jqwik.engine.discovery;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.hierarchical.Node.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.specs.*;

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

		Optional<Class<?>> optionalContainerClass = ReflectionSupport.tryToLoadClass(className).toOptional();
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
		SkipResult shouldBeSkipped = getDiscoverySpec().shouldBeSkipped(testClass);
		if (shouldBeSkipped.isSkipped()) {
			return new SkipExecutionDecorator(newContainerDescriptor, shouldBeSkipped.getReason().orElse(""));
		}
		return newContainerDescriptor;
	}

	protected abstract Class<? extends TestDescriptor> requiredParentType();

	protected abstract DiscoverySpec<Class<?>> getDiscoverySpec();

	protected abstract UniqueId createUniqueId(Class<?> testClass, TestDescriptor parent);

	protected abstract ContainerClassDescriptor createContainerDescriptor(Class<?> containerClass, UniqueId uniqueId);
}
