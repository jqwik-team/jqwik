package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
@FunctionalInterface
public interface BeforeContainerHook extends LifecycleHook {

	void beforeContainer(ContainerLifecycleContext context) throws Throwable;

	BeforeContainerHook DO_NOTHING = (containerLifecycleContext) -> {};

	default int compareTo(BeforeContainerHook other) {
		// The closer (higher proximity) the later it is called
		return Integer.compare(this.beforeContainerProximity(), other.beforeContainerProximity());
	}

	default int beforeContainerProximity() {
		return 0;
	}

}
