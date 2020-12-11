package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to define behaviour for a container (class or whole test suite)
 * that should be run exactly once after of its property methods and child containers.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface AfterContainerHook extends LifecycleHook {

	/**
	 * The code of this method will be run exactly once after all property methods and child containers.
	 *
	 * @param context The container's context object
	 */
	void afterContainer(ContainerLifecycleContext context) throws Throwable;

	/**
	 * The higher the value, the closer to the actual property methods, i.e. the earlier it will be run.
	 * Default value is 0.
	 *
	 * <p>
	 * Values greater than -10 will make it run before methods annotated with {@linkplain AfterContainer},
	 * values smaller than -10 will make it run after.
	 * </p>
	 *
	 * @return an integer value
	 */
	default int afterContainerProximity() {
		return 0;
	}

	@API(status = INTERNAL)
	AfterContainerHook DO_NOTHING = (containerLifecycleContext) -> {};

	@API(status = INTERNAL)
	default int compareTo(AfterContainerHook other) {
		// The closer (higher proximity) the earlier it is called
		return -Integer.compare(this.afterContainerProximity(), other.afterContainerProximity());
	}

}
