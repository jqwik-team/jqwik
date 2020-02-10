package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface AfterContainerHook extends LifecycleHook<AfterContainerHook> {

	void afterContainer(ContainerLifecycleContext context) throws Throwable;

	AfterContainerHook DO_NOTHING = (containerLifecycleContext) -> {};

	@Override
	default int compareTo(AfterContainerHook other) {
		// The closer (higher proximity) the earlier it is called
		return Integer.compare(this.afterContainerProximity(), other.afterContainerProximity());
	}

	default int afterContainerProximity() {
		return 0;
	}

}
