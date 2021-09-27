package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An aggregate interface of {@linkplain BeforeContainerHook} and {@linkplain AfterContainerHook}
 * for convenience.
 */
@API(status = MAINTAINED, since = "1.4.0")
@NonNullApi
public interface AroundContainerHook extends BeforeContainerHook, AfterContainerHook {

	/**
	 * @see BeforeContainerHook#beforeContainer(ContainerLifecycleContext)
	 */
	default void beforeContainer(ContainerLifecycleContext context) {}

	/**
	 * @see BeforeContainerHook#beforeContainerProximity()
	 */
	default int beforeContainerProximity() {
		return proximity();
	}

	/**
	 * @see AfterContainerHook#afterContainer(ContainerLifecycleContext)
	 */
	default void afterContainer(ContainerLifecycleContext context) {}

	/**
	 * @see AfterContainerHook#afterContainerProximity()
	 */
	default int afterContainerProximity() {
		return proximity();
	}

	/**
	 * Determine both {@linkplain #beforeContainerProximity()} and {@linkplain #afterContainerProximity()}
	 * in one go.
	 */
	default int proximity() {
		return 0;
	}
}
