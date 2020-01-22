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

	public static AroundPropertyHook combine(List<AroundPropertyHook> aroundPropertyHooks) {
		if (aroundPropertyHooks.isEmpty()) {
			return AroundPropertyHook.BASE;
		}
		aroundPropertyHooks = new ArrayList<>(aroundPropertyHooks);
		AroundPropertyHook first = aroundPropertyHooks.remove(0);
		return wrap(first, combine(aroundPropertyHooks));
	}

}
