package net.jqwik.discovery;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.SkipExecutionDecorator;
import net.jqwik.discovery.predicates.GroupDiscoverySpec;
import net.jqwik.discovery.predicates.PotentialContainerDiscoverySpec;
import net.jqwik.discovery.predicates.TopLevelContainerDiscoverySpec;
import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

class ContainerClassResolver implements ElementResolver {

	private static final PotentialContainerDiscoverySpec potentialContainerSpec = new PotentialContainerDiscoverySpec();
	private static final TopLevelContainerDiscoverySpec topLevelContainerSpec = new TopLevelContainerDiscoverySpec();
	private static final GroupDiscoverySpec groupSpec = new GroupDiscoverySpec();

	@Override
	public Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent) {
		if (!(element instanceof Class))
			return Collections.emptySet();

		Class<?> clazz = (Class<?>) element;
		if (!isPotentialCandidate(clazz))
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
		if (!isPotentialCandidate(containerClass))
			return Optional.empty();

		UniqueId uniqueId = createUniqueId(containerClass, parent);
		return Optional.of(resolveClass(containerClass, uniqueId));
	}

	protected Class<? extends TestDescriptor> requiredParentType() {
		return TestDescriptor.class;
	}

	protected boolean isPotentialCandidate(Class<?> element) {
		return potentialContainerSpec.shouldBeDiscovered(element);
	}

	protected UniqueId createUniqueId(Class<?> testClass, TestDescriptor parent) {
		return JqwikUniqueIDs.appendContainer(parent.getUniqueId(), testClass);
	}

	protected TestDescriptor resolveClass(Class<?> testClass, UniqueId uniqueId) {
		boolean isGroup = groupSpec.shouldBeDiscovered(testClass);
		ContainerClassDescriptor newContainerDescriptor = new ContainerClassDescriptor(uniqueId, testClass, isGroup);
		if (isGroup && groupSpec.butSkippedOnExecution(testClass)) {
			return new SkipExecutionDecorator(newContainerDescriptor, groupSpec.skippingReason(testClass));
		}
		if (!isGroup && topLevelContainerSpec.butSkippedOnExecution(testClass)) {
			return new SkipExecutionDecorator(newContainerDescriptor, topLevelContainerSpec.skippingReason(testClass));
		}
		return newContainerDescriptor;
	}

}
