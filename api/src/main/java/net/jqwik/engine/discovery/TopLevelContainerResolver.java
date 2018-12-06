package net.jqwik.engine.discovery;

import org.junit.platform.engine.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.specs.*;

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
