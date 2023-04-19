package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import net.jqwik.api.Tuple.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

import static net.jqwik.api.lifecycle.PropagationMode.*;

public class LifecycleHooksRegistry implements LifecycleHooksSupplier {

	private static final Logger LOG = Logger.getLogger(LifecycleHooksRegistry.class.getName());

	private static <T extends LifecycleHook> Comparator<T> dontCompare() {
		return (a, b) -> 0;
	}

	private final List<HookRegistration> registrations = new ArrayList<>();
	private final Map<Class<? extends LifecycleHook>, LifecycleHook> instances = new LinkedHashMap<>();
	private final Set<Tuple2<TestDescriptor, Class<? extends RegistrarHook>>> appliedRegistrars = new HashSet<>();

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
	public ResolveParameterHook resolveParameterHook(TestDescriptor descriptor) {
		List<ResolveParameterHook> resolveParameterHooks = findHooks(descriptor, ResolveParameterHook.class, dontCompare());
		return HookSupport.combineResolveParameterHooks(resolveParameterHooks);
	}

	@Override
	public SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor) {
		List<SkipExecutionHook> skipExecutionHooks = findHooks(testDescriptor, SkipExecutionHook.class, dontCompare());
		return HookSupport.combineSkipExecutionHooks(skipExecutionHooks);
	}

	@Override
	public InvokePropertyMethodHook invokePropertyMethodHook(TestDescriptor testDescriptor) {
		return getSingletonHook(testDescriptor, InvokePropertyMethodHook.class, InvokePropertyMethodHook.DEFAULT);
	}

	@Override
	public ProvidePropertyInstanceHook providePropertyInstanceHook(TestDescriptor testDescriptor) {
		return getSingletonHook(testDescriptor, ProvidePropertyInstanceHook.class, ProvidePropertyInstanceHook.DEFAULT);
	}

	private <T extends LifecycleHook> T getSingletonHook(TestDescriptor testDescriptor, Class<T> hookType, T defaultHook) {
		List<T> invokeMethodHooks = findHooks(testDescriptor, hookType, dontCompare());
		if (invokeMethodHooks.isEmpty()) {
			return defaultHook;
		}
		T hookToApply = invokeMethodHooks.get(0);
		if (invokeMethodHooks.size() > 1) {
			String message = String.format(
				"Test [%s] must have only one applicable [%s] but it has: %n\t%s.%nOnly [%s] is applied.",
				testDescriptor.getDisplayName(),
				hookType.getSimpleName(),
				invokeMethodHooks,
				hookToApply
			);
			LOG.warning(message);
		}
		return hookToApply;
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
		List<LifecycleHook> hooks = findHooks(descriptor, LifecycleHook.class, dontCompare());
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
				   .filter(registration -> registration.match(descriptor))
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
		createAndRegisterHook(descriptor, hookClass, hookInstance.propagateTo());
		if (!instances.containsKey(hookClass)) {
			instances.put(hookClass, hookInstance);
		}
		registerRegistrarHooks(descriptor, hookInstance);
	}

	private void createAndRegisterHook(
		TestDescriptor descriptor,
		Class<? extends LifecycleHook> hookClass,
		PropagationMode propagateTo
	) {
		HookRegistration registration = new HookRegistration(descriptor, hookClass, propagateTo);
		if (!registrations.contains(registration)) {
			registrations.add(registration);
		}
	}

	public void registerLifecycleHook(
		TestDescriptor descriptor,
		Class<? extends LifecycleHook> hookClass,
		PropagationMode propagationMode
	) {
		if (JqwikReflectionSupport.isInnerClass(hookClass)) {
			String message = String.format("Inner class [%s] cannot be used as LifecycleHook", hookClass.getName());
			throw new JqwikException(message);
		}
		if (!JqwikReflectionSupport.hasDefaultConstructor(hookClass)) {
			String message = String.format("Hook class [%s] must have default constructor", hookClass.getName());
			throw new JqwikException(message);
		}
		LifecycleHook hookInstance = instances.computeIfAbsent(
			hookClass, clazz -> CurrentTestDescriptor.runWithDescriptor(
				descriptor, () -> ReflectionSupport.newInstance(hookClass)
			)
		);
		PropagationMode propagateTo = propagationMode;
		if (propagateTo == NOT_SET) {
			propagateTo = hookInstance.propagateTo();
		}
		createAndRegisterHook(descriptor, hookClass, propagateTo);
		registerRegistrarHooks(descriptor, hookInstance);
	}

	private void registerRegistrarHooks(TestDescriptor descriptor, LifecycleHook hookInstance) {
		if (hookInstance instanceof RegistrarHook) {
			RegistrarHook registrarHook = (RegistrarHook) hookInstance;
			if (registrarHook.propagateTo() != NO_DESCENDANTS) {
				String warnAboutPropagationMode =
					String.format(
						"RegistrarHook [%s] is propagated to descendants.%nThis does not work for registerHooks()!",
						hookInstance.getClass()
					);
				LOG.warning(warnAboutPropagationMode);
			}

			Tuple2 appliedRegistrar = Tuple.of(descriptor, hookInstance.getClass());
			if (appliedRegistrars.contains(appliedRegistrar)) {
				// Prevent recursive registration
				return;
			}
			appliedRegistrars.add(appliedRegistrar);

			RegistrarHook.Registrar registrar =
				(hookClass, propagationMode) -> registerLifecycleHook(descriptor, hookClass, propagationMode);
			registrarHook.registerHooks(registrar);
		}
	}

	private static class HookRegistration {
		private final TestDescriptor descriptor;
		private final Class<? extends LifecycleHook> hookClass;
		private final PropagationMode propagationMode;

		private HookRegistration(
			TestDescriptor descriptor,
			Class<? extends LifecycleHook> hookClass,
			PropagationMode propagationMode
		) {
			if (propagationMode == NOT_SET) {
				throw new IllegalArgumentException("propagation mode must be set by caller");
			}
			this.descriptor = descriptor;
			this.hookClass = hookClass;
			this.propagationMode = propagationMode;
		}

		boolean match(TestDescriptor descriptor) {
			return match(descriptor, 0);
		}

		private boolean match(TestDescriptor descriptor, int nesting) {
			if (descriptor == null) {
				return false;
			}
			if (nesting > 0 && (propagationMode == NO_DESCENDANTS)) {
				return false;
			}
			if (nesting > 1 && (propagationMode != ALL_DESCENDANTS)) {
				return false;
			}
			if (this.descriptor.equals(descriptor)) {
				return true;
			}
			return match(descriptor.getParent().orElse(null), nesting + 1);
		}

		boolean match(Class<? extends LifecycleHook> hookType) {
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

