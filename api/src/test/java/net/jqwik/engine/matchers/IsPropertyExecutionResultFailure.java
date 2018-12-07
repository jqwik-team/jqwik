package net.jqwik.engine.matchers;

import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.api.lifecycle.*;

class IsPropertyExecutionResultFailure implements ArgumentMatcher<PropertyExecutionResult> {
	private final String message;

	IsPropertyExecutionResultFailure(String message) {
		this.message = message;
	}

	@Override
	public boolean matches(PropertyExecutionResult result) {
		if (result.getStatus() != TestExecutionResult.Status.FAILED)
			return false;
		if (message == null)
			return true;
		return result.getThrowable().get().getMessage().equals(message);
	}
}
