package net.jqwik.discovery;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.discovery.specs.DiscoverySpec;
import net.jqwik.discovery.specs.GroupDiscoverySpec;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

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
