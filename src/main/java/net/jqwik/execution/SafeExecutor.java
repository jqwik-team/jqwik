package net.jqwik.execution;

import static org.junit.platform.commons.util.BlacklistedExceptions.*;
import static org.junit.platform.engine.TestExecutionResult.*;

import org.junit.platform.engine.*;
import org.opentest4j.*;

public class SafeExecutor {

	public interface Executable {

		void execute() throws Throwable;

	}

	public TestExecutionResult executeSafely(Executable executable) {
		try {
			executable.execute();
			return successful();
		} catch (TestAbortedException e) {
			return aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

}
