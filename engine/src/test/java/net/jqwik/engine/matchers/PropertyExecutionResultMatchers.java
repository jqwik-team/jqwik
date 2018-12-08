package net.jqwik.engine.matchers;

import net.jqwik.api.lifecycle.*;

import static org.mockito.ArgumentMatchers.*;

public class PropertyExecutionResultMatchers {

	public static PropertyExecutionResult isSuccessful() {
		return argThat(argument -> argument.getStatus() == PropertyExecutionResult.Status.SUCCESSFUL);
	}

	public static PropertyExecutionResult isFailed(String message) {
		return argThat(new IsPropertyExecutionResultFailure(message));
	}

	public static PropertyExecutionResult isFailed() {
		return isFailed(null);
	}

}
