package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface AroundPropertyHook extends LifecycleHook<AroundPropertyHook> {

	PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable;

	AroundPropertyHook BASE = (propertyDescriptor, property) -> property.execute();

	@Override
	default int compareTo(AroundPropertyHook other) {
		return Integer.compare(this.aroundPropertyProximity(), other.aroundPropertyProximity());
	}

	default int aroundPropertyProximity() {
		return 0;
	}

	default AroundPropertyHook around(AroundPropertyHook inner) {
		return (context, property) -> {
			PropertyExecutor innerExecutor = () -> {
				try {
					return inner.aroundProperty(context, property);
				} catch (Throwable throwable) {
					return PropertyExecutionResult.failed(throwable, null, null);
				}
			};
			return AroundPropertyHook.this.aroundProperty(context, innerExecutor);
		};
	}

	static AroundPropertyHook combine(List<AroundPropertyHook> aroundPropertyHooks) {
		if (aroundPropertyHooks.isEmpty()) {
			return AroundPropertyHook.BASE;
		}
		AroundPropertyHook first = aroundPropertyHooks.remove(0);
		return first.around(combine(aroundPropertyHooks));
	}

}
