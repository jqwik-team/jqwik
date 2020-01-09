package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public class JqwikLifecycleRegistrator {

	private final LifecycleHooksRegistry lifecycleRegistry;
	private final Function<String, Optional<String>> parameters;

	public JqwikLifecycleRegistrator(LifecycleHooksRegistry lifecycleRegistry, ConfigurationParameters configurationParameters) {
		this.lifecycleRegistry = lifecycleRegistry;
		this.parameters = configurationParameters::get;
	}

	public void registerLifecycleHooks(TestDescriptor rootDescriptor) {
		registerGlobalHooks(rootDescriptor);
		register(rootDescriptor);
	}

	private void registerGlobalHooks(TestDescriptor rootDescriptor) {
		for (LifecycleHook<?> lifecycleHook : RegisteredLifecycleHooks.getRegisteredHooks(parameters)) {
			lifecycleRegistry.registerLifecycleInstance(rootDescriptor, lifecycleHook);
		}
	}

	private void register(TestDescriptor descriptor) {
		if (descriptor instanceof PropertyMethodDescriptor) {
			registerPropertyMethodHooks((PropertyMethodDescriptor) descriptor);
		}
		if (descriptor instanceof ContainerClassDescriptor) {
			registerContainerHooks((ContainerClassDescriptor) descriptor);
		}
		for (TestDescriptor childDescriptor : descriptor.getChildren()) {
			register(childDescriptor);
		}
	}

	private void registerContainerHooks(ContainerClassDescriptor containerClassDescriptor) {
		Class<?> containerClass = containerClassDescriptor.getContainerClass();
		registerHooks(containerClassDescriptor, containerClass);
	}

	private void registerPropertyMethodHooks(PropertyMethodDescriptor propertyMethodDescriptor) {
		Method targetMethod = propertyMethodDescriptor.getTargetMethod();
		registerHooks(propertyMethodDescriptor, targetMethod);
	}

	private void registerHooks(TestDescriptor descriptor, AnnotatedElement element) {
		List<AddLifecycleHook> addLifecycleHooks = AnnotationSupport.findRepeatableAnnotations(element, AddLifecycleHook.class);
		for (AddLifecycleHook addLifecycleHook : addLifecycleHooks) {
			lifecycleRegistry.registerLifecycleHook(descriptor, addLifecycleHook.value(), parameters);
		}
	}

}
