package net.jqwik.descriptor;

import java.util.List;

import net.jqwik.descriptor.ExampleMethodDescriptor;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

public class OverloadedMethodsErrorDescriptor extends AbstractTestDescriptor {

	private final String overloadedMethodName;
	private final Class<?> containerClass;

	public OverloadedMethodsErrorDescriptor(List<ExampleMethodDescriptor> examples, String overloadedMethodName, Class<?> containerClass, TestDescriptor parent) {
		super(parent.getUniqueId().append("error", overloadedMethodName), determineDisplayName(overloadedMethodName));
		this.overloadedMethodName = overloadedMethodName;
		this.containerClass = containerClass;
		setParent(parent);
		setSource(new ClassSource(containerClass));

		for (int i = 0; i < examples.size(); i++) {
			ExampleMethodDescriptor example = examples.get(i);
			ExampleMethodDescriptor child = new ExampleMethodDescriptor(example, i);
			this.addChild(child);
		}
	}

	private static String determineDisplayName(String methodName) {
		return String.format("%s: Overloaded methods not allowed", methodName);
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isTest() {
		return true;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public String getOverloadedMethodName() {
		return overloadedMethodName;
	}
}
