package net.jqwik.engine.discovery;

import org.junit.platform.engine.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.specs.*;

class GroupContainerResolver extends AbstractClassResolver {

	private final GroupDiscoverySpec discoverySpec = new GroupDiscoverySpec();

	@Override
	protected Class<? extends TestDescriptor> requiredParentType() {
		return ContainerClassDescriptor.class;
	}

	@Override
	protected DiscoverySpec<Class<?>> getDiscoverySpec() {
		return discoverySpec;
	}

	@Override
	protected UniqueId createUniqueId(Class<?> testClass, TestDescriptor parent) {
		return JqwikUniqueIDs.appendContainer(parent.getUniqueId(), testClass);
	}

	@Override
	protected ContainerClassDescriptor createContainerDescriptor(Class<?> containerClass, UniqueId uniqueId) {
		return new ContainerClassDescriptor(uniqueId, containerClass, true);
	}

}
