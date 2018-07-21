package net.jqwik.api.lifecycle;

import org.junit.platform.engine.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface AroundPropertyHook extends LifecycleHook {

	TestExecutionResult aroundProperty(PropertyLifecycleContext context, Callable<TestExecutionResult> property) throws Exception;

	AroundPropertyHook BASE = (propertyDescriptor, property) -> property.call();

	default AroundPropertyHook around(AroundPropertyHook inner) {
		return (context, property) -> {
			Callable<TestExecutionResult> callInner = () -> inner.aroundProperty(context, property);
			return AroundPropertyHook.this.aroundProperty(context, callInner);
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
