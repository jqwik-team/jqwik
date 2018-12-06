package net.jqwik.engine;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.recording.*;

public interface TestEngineConfiguration {
	TestRunRecorder recorder();

	TestRunData previousRun();

	Set<UniqueId> previousFailures();
}
