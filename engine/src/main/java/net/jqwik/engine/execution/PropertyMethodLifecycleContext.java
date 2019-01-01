package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

class PropertyMethodLifecycleContext implements PropertyLifecycleContext {
	private final PropertyMethodDescriptor methodDescriptor;
	private final Object testInstance;

	PropertyMethodLifecycleContext(PropertyMethodDescriptor methodDescriptor, Object testInstance) {
		this.methodDescriptor = methodDescriptor;
		this.testInstance = testInstance;
	}

	@Override
	public Method targetMethod() {
		return methodDescriptor.getTargetMethod();
	}

	@Override
	public Class containerClass() {
		return methodDescriptor.getContainerClass();
	}

	@Override
	public String label() {
		return methodDescriptor.getLabel();
	}

	@Override
	public Object testInstance() {
		return testInstance;
	}
}
