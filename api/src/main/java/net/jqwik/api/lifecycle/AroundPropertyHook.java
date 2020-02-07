package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface AroundPropertyHook extends LifecycleHook<AroundPropertyHook> {

	PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable;

	AroundPropertyHook BASE = (propertyLifecycleContext, property) -> property.execute();

	@Override
	default int compareTo(AroundPropertyHook other) {
		return Integer.compare(this.aroundPropertyProximity(), other.aroundPropertyProximity());
	}

	default int aroundPropertyProximity() {
		return 0;
	}

}
