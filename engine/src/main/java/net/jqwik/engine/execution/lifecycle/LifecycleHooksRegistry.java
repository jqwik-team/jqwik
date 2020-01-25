package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

public class LifecycleHooksRegistry implements LifecycleHooksSupplier {

	private final List<HookRegistration> registrations = new ArrayList<>();
	private final Map<Class<? extends LifecycleHook<?>>, LifecycleHook<?>> instances = new HashMap<>();

	@Override
	public AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor) {
		List<AroundPropertyHook> aroundPropertyHooks = findHooks(propertyMethodDescriptor, AroundPropertyHook.class);
		return HookSupport.combineAroundPropertyHooks(aroundPropertyHooks);
	}

	@Override
	public AroundTryHook aroundTryHook(PropertyMethodDescriptor propertyMethodDescriptor) {
		List<AroundTryHook> aroundTryHooks = findHooks(propertyMethodDescriptor, AroundTryHook.class);
		return HookSupport.combineAroundTryHooks(aroundTryHooks);
	}

	@Override
	public SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor) {
		List<SkipExecutionHook> skipExecutionHooks = findHooks(testDescriptor, SkipExecutionHook.class);
		return HookSupport.combineSkipExecutionHooks(skipExecutionHooks);
	}

	private <T extends LifecycleHook<?>> List<T> findHooks(TestDescriptor descriptor, Class<T> hookType) {
		List<Class<T>> hookClasses = findHookClasses(descriptor, hookType);
		return hookClasses
				   .stream()
				   .map(this::getHook)
				   .sorted()
				   .collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private <T extends LifecycleHook<?>> T getHook(Class<T> hookClass) {
		return (T) instances.get(hookClass);
	}

	@SuppressWarnings("unchecked")
	private <T extends LifecycleHook<?>> List<Class<T>> findHookClasses(TestDescriptor descriptor, Class<T> hookType) {
		return registrations
				   .stream()
				   .filter(registration -> registration.match(descriptor, false))
				   .filter(registration -> registration.match(hookType))
				   .map(registration -> (Class<T>) registration.hookClass)
				   .distinct()
				   .collect(Collectors.toList());
	}

	/**
	 * Use only for registering lifecycles through Java's ServiceLoader mechanism
	 */
	void registerLifecycleInstance(TestDescriptor descriptor, LifecycleHook<?> hookInstance) {
		@SuppressWarnings("unchecked")
		Class<? extends LifecycleHook<?>> hookClass = (Class<? extends LifecycleHook<?>>) hookInstance.getClass();
		createAndRegisterHook(descriptor, hookClass);
		if (!instances.containsKey(hookClass)) {
			instances.put(hookClass, hookInstance);
		}
	}

	private void createAndRegisterHook(TestDescriptor descriptor, Class<? extends LifecycleHook<?>> hookClass) {
		boolean propagateToChildren = PropagateToChildren.class.isAssignableFrom(hookClass);
		HookRegistration registration = new HookRegistration(descriptor, hookClass, propagateToChildren);
		if (!registrations.contains(registration)) {
			registrations.add(registration);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void registerLifecycleHook(
		TestDescriptor descriptor,
		Class<? extends LifecycleHook> hookClass,
		Function<String, Optional<String>> parameters
	) {
		if (JqwikReflectionSupport.isInnerClass(hookClass)) {
			String message = String.format("Inner class [%s] cannot be used as LifecycleHook", hookClass.getName());
			throw new JqwikException(message);
		}
		createAndRegisterHook(descriptor, (Class<? extends LifecycleHook<?>>) hookClass);
		if (!instances.containsKey(hookClass)) {
			LifecycleHook<?> hookInstance = ReflectionSupport.newInstance(hookClass);
			if (hookInstance instanceof Configurable) {
				((Configurable) hookInstance).configure(parameters);
			}
			instances.put((Class<? extends LifecycleHook<?>>) hookClass, hookInstance);
		}
	}

	private static class HookRegistration {
		private TestDescriptor descriptor;
		private final Class<? extends LifecycleHook<?>> hookClass;
		private boolean propagateToChildren;

		private HookRegistration(TestDescriptor descriptor, Class<? extends LifecycleHook<?>> hookClass, boolean propagateToChildren) {
			this.descriptor = descriptor;
			this.hookClass = hookClass;
			this.propagateToChildren = propagateToChildren;
		}

		boolean match(TestDescriptor descriptor, boolean fromChild) {
			if (descriptor == null) {
				return false;
			}
			if (fromChild && !propagateToChildren) {
				return false;
			}
			if (this.descriptor.equals(descriptor)) {
				return true;
			}
			return match(descriptor.getParent().orElse(null), true);
		}

		<T extends LifecycleHook<?>> boolean match(Class<? extends LifecycleHook<?>> hookType) {
			return hookType.isAssignableFrom(hookClass);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			HookRegistration that = (HookRegistration) o;

			if (!descriptor.equals(that.descriptor)) return false;
			return hookClass.equals(that.hookClass);
		}

		@Override
		public int hashCode() {
			int result = descriptor.hashCode();
			result = 31 * result + hookClass.hashCode();
			return result;
		}
	}
}

