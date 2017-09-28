package net.jqwik.matchers;

import org.junit.platform.engine.*;
import org.mockito.*;

class IsTestResultAbortion implements ArgumentMatcher<TestExecutionResult> {
	private final String message;

	IsTestResultAbortion(String message) {
		this.message = message;
	}

	@Override
	public boolean matches(TestExecutionResult result) {
		if (result.getStatus() != TestExecutionResult.Status.ABORTED)
			return false;
		if (message == null)
			return true;
		return result.getThrowable().get().getMessage().equals(message);
	}

}
