package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

class TestInstancesCreator {
	private final ContainerLifecycleContext containerLifecycleContext;
	private final Class<?> containerClass;
	private final ContainerClassDescriptor containerDescriptor;
	private final ProvidePropertyInstanceHook providePropertyInstance;

	TestInstancesCreator(
		ContainerLifecycleContext containerLifecycleContext,
		ContainerClassDescriptor containerDescriptor,
		ProvidePropertyInstanceHook providePropertyInstanceHook
	) {
		this.containerLifecycleContext = containerLifecycleContext;
		this.containerClass = containerLifecycleContext.optionalContainerClass().orElseThrow(
			() -> {
				String message = String.format("Container [%s] cannot be instantiated", containerLifecycleContext.label());
				return new JqwikException(message);
			}
		);
		this.containerDescriptor = containerDescriptor;
		this.providePropertyInstance = providePropertyInstanceHook;
	}

	TestInstances create() {
		Object instance = create_OLD();
		return new TestInstances(instance);
	}

	private Object create_OLD() {
		if (providePropertyInstance.equals(ProvidePropertyInstanceHook.DEFAULT)) {
			return create(containerClass, containerDescriptor);
		}
		try {
			return providePropertyInstance.provide(containerClass);
		} catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
			String message = String.format(
				"ProvidePropertyInstanceHook [%s] cannot provide instance for class [%s]",
				providePropertyInstance,
				containerClass
			);
			throw new JqwikException(message, throwable);
		}
	}

	private Object create(
		Class<?> instanceClass,
		TestDescriptor descriptor
	) {
		List<Constructor<?>> constructors = allAccessibleConstructors(instanceClass);
		if (constructors.size() == 0) {
			String message = String.format("Test container class [%s] has no accessible constructor", instanceClass.getName());
			throw new JqwikException(message);
		}
		if (constructors.size() > 1) {
			String message = String.format("Test container class [%s] has more than one accessible constructor", instanceClass.getName());
			throw new JqwikException(message);
		}
		Constructor<?> constructor = constructors.get(0);
		return newInstance(instanceClass, constructor, descriptor);
	}

	private Object newInstance(
		Class<?> instanceClass,
		Constructor<?> constructor,
		TestDescriptor descriptor
	) {
		if (JqwikReflectionSupport.isInnerClass(instanceClass)) {
			return newInstanceOfInnerContainer(constructor, descriptor);
		} else {
			return JqwikReflectionSupport.newInstance(
				constructor,
				resolveParameters(constructor, null)
			);
		}
	}

	private Object newInstanceOfInnerContainer(Constructor<?> constructor, TestDescriptor descriptor) {
		TestDescriptor parentDescriptor = descriptor.getParent().orElse(descriptor);
		ContainerClassDescriptor parentClassDescriptor = (ContainerClassDescriptor) parentDescriptor;
		Class<?> parentClass = parentClassDescriptor.getContainerClass();
		Object parentInstance = create(parentClass, parentDescriptor);
		return JqwikReflectionSupport.newInstance(
			constructor,
			resolveParameters(constructor, parentInstance)
		);
	}

	private List<Constructor<?>> allAccessibleConstructors(Class<?> instanceClass) {
		List<Constructor<?>> constructors = new ArrayList<>(Arrays.asList(instanceClass.getConstructors()));
		for (Constructor<?> declaredConstructor : instanceClass.getDeclaredConstructors()) {
			if (!constructors.contains(declaredConstructor)) {
				constructors.add(declaredConstructor);
			}
		}
		return constructors;
	}

	private Object[] resolveParameters(
		Constructor<?> constructor,
		Object parent
	) {
		Object[] args = new Object[constructor.getParameterCount()];
		for (int i = 0; i < args.length; i++) {
			final int index = i;
			if (index == 0 && parent != null) {
				args[index] = parent;
			} else {
				args[index] = containerLifecycleContext
								  .resolveParameter(constructor, index)
								  .map(parameterSupplier -> parameterSupplier.get(Optional.empty()))
								  .orElseThrow(() -> {
									  String info = "No matching resolver could be found";
									  return new CannotResolveParameterException(constructor.getParameters()[index], info);
								  });
			}
		}
		return args;
	}

}
