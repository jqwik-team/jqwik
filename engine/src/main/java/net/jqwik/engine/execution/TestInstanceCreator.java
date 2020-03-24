package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

class TestInstanceCreator {
	private final ContainerLifecycleContext containerLifecycleContext;
	private final Class<?> containerClass;

	TestInstanceCreator(ContainerLifecycleContext containerLifecycleContext) {
		this.containerLifecycleContext = containerLifecycleContext;
		this.containerClass = containerLifecycleContext.optionalContainerClass().orElseThrow(
			() -> {
				String message = String.format("Container [%s] cannot be instantiated", containerLifecycleContext.label());
				return new JqwikException(message);
			}
		);
	}

	Object create() {
		return create(containerClass);
	}

	private Object create(Class<?> instanceClass) {
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
		return newInstance(instanceClass, constructor);
	}

	private Object newInstance(Class<?> instanceClass, Constructor<?> constructor) {
		if (JqwikReflectionSupport.isInnerClass(instanceClass)) {
			Object parentInstance = create(instanceClass.getDeclaringClass());
			return JqwikReflectionSupport.newInstance(constructor, resolveParameters(constructor, parentInstance));
		} else {
			return JqwikReflectionSupport.newInstance(constructor, resolveParameters(constructor, null));
		}
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
								  .map(parameterSupplier -> parameterSupplier.get(containerLifecycleContext))
								  .orElseThrow(() -> {
									  String info = "No matching resolver could be found";
									  return new CannotResolveParameterException(constructor.getParameters()[index], info);
								  });
			}
		}
		return args;
	}

}
