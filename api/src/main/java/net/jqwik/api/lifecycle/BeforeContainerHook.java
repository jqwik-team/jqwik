package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface BeforeContainerHook extends LifecycleHook<BeforeContainerHook> {

	void beforeContainer(ContainerLifecycleContext context) throws Throwable;

	BeforeContainerHook BASE = (containerLifecycleContext) -> {};

	@Override
	default int compareTo(BeforeContainerHook other) {
		return Integer.compare(this.beforeContainerProximity(), other.beforeContainerProximity());
	}

	default int beforeContainerProximity() {
		return 0;
	}

}
