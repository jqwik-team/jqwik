package net.jqwik.api.lifecycle;

import java.util.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface TeardownPropertyHook extends LifecycleHook {
	void teardownProperty(PropertyLifecycleContext propertyDescriptor) throws Throwable;

	default TeardownPropertyHook after(TeardownPropertyHook previous) {
		return propertyDescriptor -> {
			try {
				previous.teardownProperty(propertyDescriptor);
			} finally {
				TeardownPropertyHook.this.teardownProperty(propertyDescriptor);
			}
		};
	}

	static TeardownPropertyHook combine(List<TeardownPropertyHook> hooks) {
		if (hooks.isEmpty()) {
			return propertyDescriptor -> { };
		}
		TeardownPropertyHook first = hooks.remove(0);
		return first.after(combine(hooks));
	}

}
