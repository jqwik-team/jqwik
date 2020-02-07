package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public class HookSupport {

	public static AroundPropertyHook combineAroundPropertyHooks(List<AroundPropertyHook> aroundPropertyHooks) {
		if (aroundPropertyHooks.isEmpty()) {
			return AroundPropertyHook.BASE;
		}
		aroundPropertyHooks = new ArrayList<>(aroundPropertyHooks);
		AroundPropertyHook first = aroundPropertyHooks.remove(0);
		return wrap(first, combineAroundPropertyHooks(aroundPropertyHooks));
	}

	private static AroundPropertyHook wrap(AroundPropertyHook outer, AroundPropertyHook inner) {
		return (context, property) -> {
			PropertyExecutor innerExecutor = () -> inner.aroundProperty(context, property);
			return outer.aroundProperty(context, innerExecutor);
		};
	}

	public static AroundTryHook combineAroundTryHooks(List<AroundTryHook> aroundTryHooks) {
		if (aroundTryHooks.isEmpty()) {
			return AroundTryHook.BASE;
		}
		aroundTryHooks = new ArrayList<>(aroundTryHooks);
		AroundTryHook first = aroundTryHooks.remove(0);
		return wrap(first, combineAroundTryHooks(aroundTryHooks));
	}

	private static AroundTryHook wrap(AroundTryHook outer, AroundTryHook inner) {
		return (context, aTry, outerParams) -> {
			TryExecutor innerExecutor = (innerParams) -> {
				try {
					return inner.aroundTry(context, aTry, innerParams);
				} catch (Throwable throwable) {
					return JqwikExceptionSupport.throwAsUncheckedException(throwable);
				}
			};
			return outer.aroundTry(context, innerExecutor, outerParams);
		};
	}

	public static SkipExecutionHook combineSkipExecutionHooks(List<SkipExecutionHook> skipExecutionHooks) {
		if (skipExecutionHooks.isEmpty()) {
			return descriptor -> SkipExecutionHook.SkipResult.doNotSkip();
		}
		SkipExecutionHook first = skipExecutionHooks.remove(0);
		return then(first, combineSkipExecutionHooks(skipExecutionHooks));
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

	public static BeforeContainerHook combineBeforeContainerHooks(List<BeforeContainerHook> beforeContainerHooks) {
		return context -> {

			ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);

			for (BeforeContainerHook hook : beforeContainerHooks) {
				throwableCollector.execute(() -> {
					hook.beforeContainer(context);
				});
			}

			if (throwableCollector.isNotEmpty()) {
				throw throwableCollector.getThrowable();
			}

		};
	}
}
