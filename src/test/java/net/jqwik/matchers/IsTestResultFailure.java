package net.jqwik.matchers;

import org.junit.platform.engine.TestExecutionResult;
import org.mockito.ArgumentMatcher;

class IsTestResultFailure extends ArgumentMatcher<TestExecutionResult> {
	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != TestExecutionResult.class)
			return false;
		TestExecutionResult result = (TestExecutionResult) argument;
		return result.getStatus() == TestExecutionResult.Status.FAILED;
	}
}
