package net.jqwik.matchers;

import org.hamcrest.*;
import org.junit.platform.engine.*;
import org.mockito.*;

class IsTestResultAbortion extends ArgumentMatcher<TestExecutionResult> {
	private final String message;

	IsTestResultAbortion(String message) {
		this.message = message;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != TestExecutionResult.class)
			return false;
		TestExecutionResult result = (TestExecutionResult) argument;
		if (result.getStatus() != TestExecutionResult.Status.ABORTED)
			return false;
		if (message == null)
			return true;
		return result.getThrowable().get().getMessage().equals(message);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is expected to abort");
		if (message != null) {
			description.appendText(String.format(" with message '%s'", message));
		}
	}

}
