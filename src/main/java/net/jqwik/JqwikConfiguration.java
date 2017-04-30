package net.jqwik;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.recording.*;

public class JqwikConfiguration {

	private final JqwikProperties properties;

	JqwikConfiguration() {
		this(new JqwikProperties());
	}

	JqwikConfiguration(JqwikProperties properties) {
		this.properties = properties;
	}

	public TestEngineConfiguration testEngineConfiguration() {
		TestRunDatabase database = new TestRunDatabase(Paths.get(properties.getDatabasePath()));
		TestRunData previousRun = database.previousRun();
		return new TestEngineConfiguration() {
			@Override
			public TestRunRecorder recorder() {
				if (needsDatabaseAtAll())
					return database.recorder();
				return testRun -> {
				};
			}

			@Override
			public TestRunData previousRun() {
				if (!properties.rerunFailuresWithSameSeed())
					return new TestRunData();
				return database.previousRun();
			}

			@Override
			public Set<UniqueId> previousFailures() {
				if (!properties.runFailuresFirst())
					return Collections.emptySet();
				return previousRun.allNonSuccessfulTests().map(testRun -> testRun.getUniqueId()).collect(Collectors.toSet());
			}
		};
	}

	private boolean needsDatabaseAtAll() {
		return properties.rerunFailuresWithSameSeed() || properties.runFailuresFirst();
	}

}
