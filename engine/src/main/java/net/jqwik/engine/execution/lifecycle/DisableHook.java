package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

// TODO: Implement and register
public class DisableHook {

	// TODO: Replace with generic mechanism as soon as there exists one
	// see https://github.com/junit-team/junit5/issues/1717
	private boolean isSkippingDisabledUsingJupiterConfigParameter(ConfigurationParameters configurationParameters) {
		return configurationParameters
				   .get("junit.jupiter.conditions.deactivate")
				   .map(value -> value.endsWith("DisabledCondition"))
				   .orElse(false);
	}

}
