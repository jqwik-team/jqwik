package net.jqwik.descriptor;

import net.jqwik.discovery.predicates.TopLevelContainerDiscoverySpec;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

public class ContainerClassDescriptor extends AbstractTestDescriptor {

	private final Class<?> containerClass;
	private final boolean isGroup;

	private final static TopLevelContainerDiscoverySpec topLevelContainerSpec= new TopLevelContainerDiscoverySpec();

	public ContainerClassDescriptor(UniqueId uniqueId, Class<?> containerClass, boolean isGroup) {
		super(uniqueId, determineDisplayName(containerClass));
		this.containerClass = containerClass;
		this.isGroup = isGroup;
		setSource(new ClassSource(containerClass));
	}

	private static String determineDisplayName(Class<?> containerClass) {
		if (!topLevelContainerSpec.shouldBeDiscovered(containerClass))
			return containerClass.getSimpleName();
		return containerClass.getTypeName();
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isTest() {
		return false;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public boolean isGroup() {
		return isGroup;
	}
}
