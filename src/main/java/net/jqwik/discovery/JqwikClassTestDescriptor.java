package net.jqwik.discovery;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

public class JqwikClassTestDescriptor extends AbstractTestDescriptor {
	private final Class<?> containerClass;

	public JqwikClassTestDescriptor(Class<?> containerClass, TestDescriptor parent) {
		super(parent.getUniqueId().append("class", containerClass.getName()), determineDisplayName(containerClass));
		this.containerClass = containerClass;
		setParent(parent);
		setSource(new ClassSource(containerClass));
	}

	private static String determineDisplayName(Class<?> containerClass) {
		return containerClass.getSimpleName();
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isTest() {
		return false;
	}
}
