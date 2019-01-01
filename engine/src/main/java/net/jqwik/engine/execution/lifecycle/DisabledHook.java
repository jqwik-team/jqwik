package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;

// TODO: Implement and register
public class DisabledHook implements SkipExecutionHook {

	private boolean isSkippingDisabled;

	@Override
	public SkipResult shouldBeSkipped(LifecycleContext context) {
		// TODO: Implement if Disabled annotation is present
		return SkipResult.doNotSkip();
	}

	@Override
	public void configure(Function<String, Optional<String>> parameters) {
		isSkippingDisabled = isSkippingDisabledUsingJupiterConfigParameter(parameters);
	}

	// TODO: Replace with generic mechanism as soon as there exists one
	// see https://github.com/junit-team/junit5/issues/1717
	private boolean isSkippingDisabledUsingJupiterConfigParameter(Function<String, Optional<String>> parameters) {
		return parameters
				   .apply("junit.jupiter.conditions.deactivate")
				   .map(value -> value.endsWith("DisabledCondition"))
				   .orElse(false);
	}

}
