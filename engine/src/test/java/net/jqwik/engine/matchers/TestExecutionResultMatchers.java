package net.jqwik.engine.matchers;

import org.junit.platform.engine.*;

import static org.mockito.ArgumentMatchers.*;

public class TestExecutionResultMatchers {

	public static TestExecutionResult isSuccessful() {
		return eq(TestExecutionResult.successful());
	}

	public static TestExecutionResult isFailed(String message) {
		return argThat(new IsTestResultFailure(message));
	}
}
