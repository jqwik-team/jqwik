package net.jqwik.engine;

import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.support.*;
import net.jqwik.engine.recording.*;

public class DefaultJqwikConfiguration implements JqwikConfiguration {

	private static final Logger LOG = Logger.getLogger(JqwikConfiguration.class.getName());

	private final JqwikProperties properties;
	private TestEngineConfiguration testEngineConfiguration = null;

	DefaultJqwikConfiguration(ConfigurationParameters configurationParameters) {
		this(JqwikProperties.load(configurationParameters));
	}

	private DefaultJqwikConfiguration(JqwikProperties properties) {
		this.properties = properties;
	}

	@Override
	public PropertyAttributesDefaults propertyDefaultValues() {
		return PropertyAttributesDefaults.with(
			properties.defaultTries(),
			properties.defaultMaxDiscardRatio(),
			properties.defaultAfterFailure(),
			properties.defaultGeneration(),
			properties.defaultEdgeCases(),
			properties.defaultShrinking(),
			properties.boundedShrinkingSeconds(),
			properties.fixedSeedMode(),
			properties.defaultSeed()
		);
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
		String databasePath = properties.databasePath();
		if (databasePath == null || databasePath.trim().isEmpty()) {
			LOG.log(Level.INFO, "jqwik's test run database has been switched off");
			return testEngineConfigurationWithDisabledDatabase();
		}
		return testEngineConfigurationFromDatabase(databasePath);
	}

	private TestEngineConfiguration testEngineConfigurationWithDisabledDatabase() {
		return new TestEngineConfiguration() {
			@Override
			public TestRunRecorder recorder() {
				return TestRunRecorder.NULL;
			}

			@Override
			public TestRunData previousRun() {
				return new TestRunData();
			}

			@Override
			public Set<UniqueId> previousFailures() {
				return Collections.emptySet();
			}
		};
	}

	private TestEngineConfiguration testEngineConfigurationFromDatabase(String databasePath) {
		TestRunDatabase database = new TestRunDatabase(Paths.get(databasePath));
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
				return previousRun.allNonSuccessfulTests().map(TestRun::getUniqueId).collect(CollectorsSupport.toLinkedHashSet());
			}
		};
	}
}
