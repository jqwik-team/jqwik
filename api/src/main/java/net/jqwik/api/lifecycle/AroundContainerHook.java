package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface AroundContainerHook extends BeforeContainerHook, AfterContainerHook {

	default void beforeContainer(ContainerLifecycleContext context) {}

	default void afterContainer(ContainerLifecycleContext context) {}

	default int afterContainerProximity() {
		return proximity();
	}

	default int beforeContainerProximity() {
		return proximity();
	}

	default int proximity() {
		return 0;
	}
}
