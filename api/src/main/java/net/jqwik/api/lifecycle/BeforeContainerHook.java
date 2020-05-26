package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to define behaviour for a container (class or whole test suite)
 * that should be run exactly once before any of its property methods and child containers.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
@FunctionalInterface
public interface BeforeContainerHook extends LifecycleHook {

	/**
	 * The code of this method will be run exactly once before any property method or child container.
	 *
	 * @param context The container's context object
	 */
	void beforeContainer(ContainerLifecycleContext context) throws Throwable;

	/**
	 * The higher value, the closer to the actual property methods, i.e. the later it will be run.
	 * Default value is 0.
	 *
	 * <p>
	 * Values greater than -10 will make it run after methods annotated with {@linkplain BeforeContainer},
	 * values smaller than -10 will make it run before.
	 * </p>
	 *
	 * @return an integer value
	 */
	default int beforeContainerProximity() {
		return 0;
	}

	@API(status = INTERNAL)
	BeforeContainerHook DO_NOTHING = (containerLifecycleContext) -> {};

	@API(status = INTERNAL)
	default int compareTo(BeforeContainerHook other) {
		// The closer (higher proximity) the later it is called
		return Integer.compare(this.beforeContainerProximity(), other.beforeContainerProximity());
	}

}
