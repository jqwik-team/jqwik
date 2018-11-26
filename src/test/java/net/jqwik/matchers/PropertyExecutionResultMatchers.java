package net.jqwik.matchers;

import net.jqwik.execution.*;

import static org.mockito.ArgumentMatchers.*;

public class PropertyExecutionResultMatchers {

	public static PropertyExecutionResult isSuccessful() {
		return eq(PropertyExecutionResult.successful());
	}

	public static PropertyExecutionResult isFailed(String message) {
		return argThat(new IsPropertyExecutionResultFailure(message));
	}

	public static PropertyExecutionResult isFailed() {
		return isFailed(null);
	}

}
