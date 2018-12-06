package net.jqwik.engine.matchers;

import org.junit.platform.engine.*;

import net.jqwik.engine.execution.*;

import static org.mockito.ArgumentMatchers.*;

public class PropertyExecutionResultMatchers {

	public static PropertyExecutionResult isSuccessful() {
		return argThat(argument -> argument.getStatus() == TestExecutionResult.Status.SUCCESSFUL);
	}

	public static PropertyExecutionResult isFailed(String message) {
		return argThat(new IsPropertyExecutionResultFailure(message));
	}

	public static PropertyExecutionResult isFailed() {
		return isFailed(null);
	}

}
