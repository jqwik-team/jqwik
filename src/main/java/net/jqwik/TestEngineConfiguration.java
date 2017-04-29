package net.jqwik;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.recording.*;

public interface TestEngineConfiguration {
	TestRunRecorder recorder();

	TestRunData previousRun();

	Set<UniqueId> previousFailures();
}
