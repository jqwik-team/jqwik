package net.jqwik.discovery;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.discovery.specs.DiscoverySpec;
import net.jqwik.discovery.specs.TopLevelContainerDiscoverySpec;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

class TopLevelContainerResolver extends AbstractClassResolver {

	private final TopLevelContainerDiscoverySpec discoverySpec = new TopLevelContainerDiscoverySpec();

	protected Class<? extends TestDescriptor> requiredParentType() {
		return TestDescriptor.class;
	}

	protected DiscoverySpec<Class<?>> getDiscoverySpec() {
		return discoverySpec;
	}

	protected UniqueId createUniqueId(Class<?> testClass, TestDescriptor parent) {
		return JqwikUniqueIDs.appendContainer(parent.getUniqueId(), testClass);
	}

	protected ContainerClassDescriptor createContainerDescriptor(Class<?> containerClass, UniqueId uniqueId) {
		return new ContainerClassDescriptor(uniqueId, containerClass, false);
	}

}
