package net.jqwik.engine.hooks;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

public class DisabledHook implements SkipExecutionHook, PropagateToChildren {

	@Override
	public SkipResult shouldBeSkipped(LifecycleContext context) {
		return context.annotatedElement()
					  .flatMap(element -> AnnotationSupport.findAnnotation(element, Disabled.class))
					  .map(disabled -> {
						  String reason = disabled.value().isEmpty() ?
											  String.format("@Disabled: %s", context.annotatedElement().get())
											  : disabled.value();
						  return SkipResult.skip(reason);
					  })
					  .orElse(SkipResult.doNotSkip());
	}

	@Override
	public int order() {
		return Hooks.SkipExecution.DISABLED_ORDER;
	}
}
