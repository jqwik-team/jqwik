package net.jqwik.api.lifecycle;

import java.util.*;

import org.junit.platform.engine.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface AroundPropertyHook extends LifecycleHook {

	TestExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable;

	AroundPropertyHook BASE = (propertyDescriptor, property) -> property.execute();

	default AroundPropertyHook around(AroundPropertyHook inner) {
		return (context, property) -> {
			PropertyExecutor innerExecutor = () -> {
				try {
					return inner.aroundProperty(context, property);
				} catch (Throwable throwable) {
					return TestExecutionResult.failed(throwable);
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
