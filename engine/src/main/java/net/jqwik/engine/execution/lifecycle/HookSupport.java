package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.junit.platform.engine.support.hierarchical.*;
import org.opentest4j.*;

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
			PropertyExecutor innerExecutor = () -> {
				try {
					return inner.aroundProperty(context, property);
				} catch (Throwable throwable) {
					return JqwikExceptionSupport.throwAsUncheckedException(throwable);
				}
			};
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
			return SkipExecutionHook.DO_NOT_SKIP;
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
			ThrowableCollector throwableCollector = new ThrowableCollector(TestAbortedException.class::isInstance);
			for (BeforeContainerHook hook : beforeContainerHooks) {
				throwableCollector.execute(() -> {
					hook.beforeContainer(context);
				});
			}
			throwableCollector.assertEmpty();
		};
	}

	public static AfterContainerHook combineAfterContainerHooks(List<AfterContainerHook> afterContainerHooks) {
		return context -> {
			ThrowableCollector throwableCollector = new ThrowableCollector(TestAbortedException.class::isInstance);
			for (AfterContainerHook hook : afterContainerHooks) {
				throwableCollector.execute(() -> {
					hook.afterContainer(context);
				});
			}
			throwableCollector.assertEmpty();
		};
	}

	public static ResolveParameterHook combineResolveParameterHooks(List<ResolveParameterHook> resolveParameterHooks) {
		if (resolveParameterHooks.isEmpty()) {
			return ResolveParameterHook.DO_NOT_RESOLVE;
		}
		return new CombinedResolveParameterHook(resolveParameterHooks);
	}

}
