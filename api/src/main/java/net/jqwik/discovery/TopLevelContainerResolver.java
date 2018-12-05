package net.jqwik.discovery;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.specs.*;
import org.junit.platform.engine.*;

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
