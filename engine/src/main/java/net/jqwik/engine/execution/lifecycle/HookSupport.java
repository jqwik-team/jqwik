package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;

public class HookSupport {

	private static AroundPropertyHook wrap(AroundPropertyHook outer, AroundPropertyHook inner) {
		return (context, property) -> {
			PropertyExecutor innerExecutor = () -> inner.aroundProperty(context, property);
			return outer.aroundProperty(context, innerExecutor);
		};
	}

	public static AroundPropertyHook combineAroundPropertyHooks(List<AroundPropertyHook> aroundPropertyHooks) {
		if (aroundPropertyHooks.isEmpty()) {
			return AroundPropertyHook.BASE;
		}
		aroundPropertyHooks = new ArrayList<>(aroundPropertyHooks);
		AroundPropertyHook first = aroundPropertyHooks.remove(0);
		return wrap(first, combineAroundPropertyHooks(aroundPropertyHooks));
	}

	private static SkipExecutionHook then(SkipExecutionHook first, SkipExecutionHook rest) {
		return descriptor -> {
			SkipExecutionHook.SkipResult result = first.shouldBeSkipped(descriptor);
			if (result.isSkipped()) {
				return result;
			} else {
				return rest.shouldBeSkipped(descriptor);
			}
		};
	}

	public static SkipExecutionHook combineSkipExecutionHooks(List<SkipExecutionHook> skipExecutionHooks) {
		if (skipExecutionHooks.isEmpty()) {
			return descriptor -> SkipExecutionHook.SkipResult.doNotSkip();
		}
		SkipExecutionHook first = skipExecutionHooks.remove(0);
		return then(first, combineSkipExecutionHooks(skipExecutionHooks));
	}

}
