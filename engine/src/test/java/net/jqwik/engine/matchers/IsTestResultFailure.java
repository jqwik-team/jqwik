package net.jqwik.engine.matchers;

import org.junit.platform.engine.*;
import org.mockito.*;

class IsTestResultFailure implements ArgumentMatcher<TestExecutionResult> {
	private final String message;

	IsTestResultFailure(String message) {
		this.message = message;
	}

	@Override
	public boolean matches(TestExecutionResult result) {
		if (result.getStatus() != TestExecutionResult.Status.FAILED)
			return false;
		if (message == null)
			return true;
		return result.getThrowable().get().getMessage().equals(message);
	}
}
