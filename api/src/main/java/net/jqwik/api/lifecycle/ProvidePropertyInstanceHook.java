package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to override behaviour that provides the test instance for each property try.
 * Per default the only accessible constructor of the container class is invoked,
 * with all parameters being resolved through applicable {@linkplain ResolveParameterHook} hooks.
 *
 * <p>
 * This hook can only be used in simple cases, since parameter resolution is currently
 * unavailable to implementors.
 * </p>
 * <p>
 * Caveat: Only one hook per property method is possible.
 * </p>
 */
@API(status = EXPERIMENTAL, since = "1.6.0")
@FunctionalInterface
public interface ProvidePropertyInstanceHook extends LifecycleHook {

	// TODO: Make that a proper invokable implementation.
	//       May require to add additional parameter(s).
	ProvidePropertyInstanceHook DEFAULT = containerClass -> {
		throw new RuntimeException("ProvideTestInstanceHook.DEFAULT must never be invoked. Return hook instead");
	};

	/**
	 * Create the test instance for running a property or example.
	 * You may want to override the default call if provision does something else than plain
	 * instance creation from class.
	 *
	 * <p>This method will be called exactly once per property. All tries share the same instance!</p>
	 *
	 * @param containerClass The container class which runs the test
	 * @return an object that must be assignable to {@code containerClass}
	 */
	Object provide(Class<?> containerClass) throws Throwable;
}
