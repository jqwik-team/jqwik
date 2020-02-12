package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

public class LifecycleHooksRegistry implements LifecycleHooksSupplier {

	private static final Comparator<LifecycleHook> DONT_COMPARE = (a, b) -> 0;

	private final List<HookRegistration> registrations = new ArrayList<>();
	private final Map<Class<? extends LifecycleHook>, LifecycleHook> instances = new HashMap<>();

	@Override
	public AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor) {
		List<AroundPropertyHook> aroundPropertyHooks = findHooks(propertyMethodDescriptor, AroundPropertyHook.class, AroundPropertyHook::compareTo);
		return HookSupport.combineAroundPropertyHooks(aroundPropertyHooks);
	}

	@Override
	public AroundTryHook aroundTryHook(PropertyMethodDescriptor propertyMethodDescriptor) {
		List<AroundTryHook> aroundTryHooks = findHooks(propertyMethodDescriptor, AroundTryHook.class, AroundTryHook::compareTo);
		return HookSupport.combineAroundTryHooks(aroundTryHooks);
	}

	@Override
	public BeforeContainerHook beforeContainerHook(TestDescriptor descriptor) {
		List<BeforeContainerHook> beforeContainerHooks = findHooks(descriptor, BeforeContainerHook.class, BeforeContainerHook::compareTo);
		return HookSupport.combineBeforeContainerHooks(beforeContainerHooks);
	}

	@Override
	public AfterContainerHook afterContainerHook(TestDescriptor descriptor) {
		List<AfterContainerHook> afterContainerHooks = findHooks(descriptor, AfterContainerHook.class, AfterContainerHook::compareTo);
		return HookSupport.combineAfterContainerHooks(afterContainerHooks);
	}

	@Override
	public ResolveParameterHook injectParameterHook(PropertyMethodDescriptor propertyMethodDescriptor) {
		List<ResolveParameterHook> injectParameterHooks = findHooks(propertyMethodDescriptor, ResolveParameterHook.class, ResolveParameterHook::compareTo);
		return HookSupport.combineInjectParameterHooks(injectParameterHooks);
	}

	@Override
	public SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor) {
		List<SkipExecutionHook> skipExecutionHooks = findHooks(testDescriptor, SkipExecutionHook.class, SkipExecutionHook::compareTo);
		return HookSupport.combineSkipExecutionHooks(skipExecutionHooks);
	}

	@Override
	public void prepareHooks(TestDescriptor descriptor) {
		for (LifecycleHook hook : findHooks(descriptor, LifecycleHook.class, DONT_COMPARE)) {
			hook.prepareFor(elementFor(descriptor));
		}
	}

	private <T extends LifecycleHook> List<T> findHooks(TestDescriptor descriptor, Class<T> hookType, Comparator<T> comparator) {
		List<Class<T>> hookClasses = findHookClasses(descriptor, hookType);
		return hookClasses
				   .stream()
				   .map(this::getHook)
				   .filter(hook -> hookAppliesTo(hook, descriptor))
				   .sorted(comparator)
				   .collect(Collectors.toList());
	}

	private <T extends LifecycleHook> boolean hookAppliesTo(T hook, TestDescriptor descriptor) {
		Optional<AnnotatedElement> element = elementFor(descriptor);
		return hook.appliesTo(element);
	}

	private Optional<AnnotatedElement> elementFor(TestDescriptor descriptor) {
		Optional<AnnotatedElement> element = Optional.empty();
		if (descriptor instanceof JqwikDescriptor) {
			element = Optional.of(((JqwikDescriptor) descriptor).getAnnotatedElement());
		}
		return element;
	}

	/*
	 * For testing only
	 */
	public <T extends LifecycleHook> boolean hasHook(TestDescriptor descriptor, Class<T> concreteHook) {
		List<LifecycleHook> hooks = findHooks(descriptor, LifecycleHook.class, DONT_COMPARE);
		return hooks.stream().anyMatch(hook -> hook.getClass().equals(concreteHook));
	}

	@SuppressWarnings("unchecked")
	private <T extends LifecycleHook> T getHook(Class<T> hookClass) {
		return (T) instances.get(hookClass);
	}

	@SuppressWarnings("unchecked")
	private <T extends LifecycleHook> List<Class<T>> findHookClasses(TestDescriptor descriptor, Class<T> hookType) {
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
	void registerLifecycleInstance(TestDescriptor descriptor, LifecycleHook hookInstance) {
		Class<? extends LifecycleHook> hookClass = hookInstance.getClass();
		createAndRegisterHook(descriptor, hookClass);
		if (!instances.containsKey(hookClass)) {
			instances.put(hookClass, hookInstance);
		}
	}

	private void createAndRegisterHook(TestDescriptor descriptor, Class<? extends LifecycleHook> hookClass) {
		boolean propagateToChildren = ApplyToChildren.class.isAssignableFrom(hookClass);
		HookRegistration registration = new HookRegistration(descriptor, hookClass, propagateToChildren);
		if (!registrations.contains(registration)) {
			registrations.add(registration);
		}
	}

	public void registerLifecycleHook(TestDescriptor descriptor, Class<? extends LifecycleHook> hookClass) {
		if (JqwikReflectionSupport.isInnerClass(hookClass)) {
			String message = String.format("Inner class [%s] cannot be used as LifecycleHook", hookClass.getName());
			throw new JqwikException(message);
		}
		if (!JqwikReflectionSupport.hasDefaultConstructor(hookClass)) {
			String message = String.format("Hook class [%s] must have default constructor", hookClass.getName());
			throw new JqwikException(message);
		}
		createAndRegisterHook(descriptor, hookClass);
		if (!instances.containsKey(hookClass)) {
			CurrentTestDescriptor.runWithDescriptor(descriptor, () -> {
				LifecycleHook hookInstance = ReflectionSupport.newInstance(hookClass);
				instances.put(hookClass, hookInstance);
			});
		}
	}

	private static class HookRegistration {
		private TestDescriptor descriptor;
		private final Class<? extends LifecycleHook> hookClass;
		private boolean propagateToChildren;

		private HookRegistration(
			TestDescriptor descriptor,
			Class<? extends LifecycleHook> hookClass,
			boolean propagateToChildren
		) {
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

		<T extends LifecycleHook> boolean match(Class<? extends LifecycleHook> hookType) {
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

