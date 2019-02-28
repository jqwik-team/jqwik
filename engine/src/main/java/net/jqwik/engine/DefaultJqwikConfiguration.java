package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.recording.*;

public class DefaultJqwikConfiguration implements JqwikConfiguration {

	private final JqwikProperties properties;
	private TestEngineConfiguration testEngineConfiguration = null;

	DefaultJqwikConfiguration() {
		this(new JqwikProperties());
	}

	private DefaultJqwikConfiguration(JqwikProperties properties) {
		this.properties = properties;
	}

	@Override
	public PropertyDefaultValues propertyDefaultValues() {
		return PropertyDefaultValues.with(properties.defaultTries(), properties.defaultMaxDiscardRatio(), properties.defaultAfterFailure());
	}

	@Override
	public TestEngineConfiguration testEngineConfiguration() {
		if (null == testEngineConfiguration) {
			testEngineConfiguration = createTestEngineConfiguration();
		}
		return testEngineConfiguration;
	}

	@Override
	public boolean useJunitPlatformReporter() {
		return properties.useJunitPlatformReporter();
	}

	@Override
	public boolean reportOnlyFailures() {
		return properties.reportOnlyFailures();
	}

	private TestEngineConfiguration createTestEngineConfiguration() {
		TestRunDatabase database = new TestRunDatabase(Paths.get(properties.databasePath()));
		TestRunData previousRun = database.previousRun();
		return new TestEngineConfiguration() {
			@Override
			public TestRunRecorder recorder() {
				return database.recorder();
			}

			@Override
			public TestRunData previousRun() {
				return database.previousRun();
			}

			@Override
			public Set<UniqueId> previousFailures() {
				if (!properties.runFailuresFirst())
					return Collections.emptySet();
				return previousRun.allNonSuccessfulTests().map(TestRun::getUniqueId).collect(Collectors.toSet());
			}
		};
	}
}
