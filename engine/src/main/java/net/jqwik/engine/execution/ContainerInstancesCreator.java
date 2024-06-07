package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import org.jspecify.annotations.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

class ContainerInstancesCreator {
	private final ContainerLifecycleContext containerLifecycleContext;
	private final Class<?> containerClass;
	private final ContainerClassDescriptor containerDescriptor;
	private final ProvidePropertyInstanceHook providePropertyInstance;

	ContainerInstancesCreator(
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

	ContainerInstances create() {
		if (providePropertyInstance.equals(ProvidePropertyInstanceHook.DEFAULT)) {
			return createInstances(containerClass, containerDescriptor);
		} else {
			try {
				return new ContainerInstances(providePropertyInstance.provide(containerClass));
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
	}

	private ContainerInstances createInstances(
		Class<?> targetClass,
		TestDescriptor descriptor
	) {
		List<Constructor<?>> constructors = allAccessibleConstructors(targetClass);
		if (constructors.size() == 0) {
			String message = String.format("Test container class [%s] has no accessible constructor", targetClass.getName());
			throw new JqwikException(message);
		}
		if (constructors.size() > 1) {
			String message = String.format("Test container class [%s] has more than one accessible constructor", targetClass.getName());
			throw new JqwikException(message);
		}
		Constructor<?> constructor = constructors.get(0);
		return newInstances(targetClass, constructor, descriptor);
	}

	private ContainerInstances newInstances(
		Class<?> targetClass,
		Constructor<?> constructor,
		TestDescriptor descriptor
	) {
		if (JqwikReflectionSupport.isInnerClass(targetClass)) {
			return newInstancesOfInnerContainer(constructor, descriptor);
		} else {
			return newInstancesOfBaseContainer(constructor);
		}
	}

	private ContainerInstances newInstancesOfBaseContainer(Constructor<?> constructor) {
		return new ContainerInstances(createNewInstance(constructor, null));
	}

	private Object createNewInstance(Constructor<?> constructor, @Nullable Object outerInstance) {
		return JqwikReflectionSupport.newInstance(
			constructor,
			resolveConstructorParameters(constructor, outerInstance)
		);
	}

	private ContainerInstances newInstancesOfInnerContainer(Constructor<?> constructor, TestDescriptor descriptor) {
		TestDescriptor parentDescriptor = descriptor.getParent().orElse(descriptor);
		ContainerClassDescriptor parentClassDescriptor = (ContainerClassDescriptor) parentDescriptor;
		Class<?> parentClass = parentClassDescriptor.getContainerClass();
		ContainerInstances parentInstances = createInstances(parentClass, parentDescriptor);
		return new ContainerInstances(createNewInstance(constructor, parentInstances.target()), parentInstances);
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

	private Object[] resolveConstructorParameters(
		Constructor<?> constructor,
		@Nullable Object outerInstance
	) {
		Object[] args = new Object[constructor.getParameterCount()];
		for (int i = 0; i < args.length; i++) {
			final int index = i;
			if (index == 0 && outerInstance != null) {
				args[index] = outerInstance;
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
