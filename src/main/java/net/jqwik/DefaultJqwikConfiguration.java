package net.jqwik;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.recording.*;

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
		return PropertyDefaultValues.with(properties.defaultTries(), properties.defaultMaxDiscardRatio());
	}

	@Override
	public TestEngineConfiguration testEngineConfiguration() {
		if (null == testEngineConfiguration) {
			testEngineConfiguration = createTestEngineConfiguration();
		}
		return testEngineConfiguration;
	}

	private TestEngineConfiguration createTestEngineConfiguration() {
		TestRunDatabase database = new TestRunDatabase(Paths.get(properties.databasePath()));
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
				return previousRun.allNonSuccessfulTests().map(TestRun::getUniqueId).collect(Collectors.toSet());
			}
		};
	}

	private boolean needsDatabaseAtAll() {
		return properties.rerunFailuresWithSameSeed() || properties.runFailuresFirst();
	}

}
