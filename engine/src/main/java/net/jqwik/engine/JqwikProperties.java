package net.jqwik.engine;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class JqwikProperties {

	public static final int DEFAULT_TRIES = 1000;

	private static final String[] SUPPORTED_PROPERTIES = new String[]{
			"database",
			"runFailuresFirst",
			"defaultTries",
			"defaultMaxDiscardRatio",
			"useJunitPlatformReporter",
			"defaultAfterFailure",
			"reportOnlyFailures",
			"defaultGeneration",
			"defaultEdgeCases",
			"defaultShrinking",
			"boundedShrinkingSeconds"
	};

	private static final String PROPERTIES_FILE_NAME = "jqwik.properties";
	private static final String CONFIGURATION_PARAMETERS_PREFIX = "jqwik.";
	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private static final String DEFAULT_DATABASE_PATH = ".jqwik-database";
	private static final boolean DEFAULT_RERUN_FAILURES_FIRST = false;
	private static final int DEFAULT_MAX_DISCARD_RATIO = 5;
	private static final AfterFailureMode DEFAULT_AFTER_FAILURE = AfterFailureMode.PREVIOUS_SEED;
	private static final boolean DEFAULT_REPORT_ONLY_FAILURES = false;
	private static final GenerationMode DEFAULT_GENERATION = GenerationMode.AUTO;
	private static final EdgeCasesMode DEFAULT_EDGE_CASES = EdgeCasesMode.MIXIN;
	private static final ShrinkingMode DEFAULT_SHRINKING = ShrinkingMode.BOUNDED;
	private static final int DEFAULT_BOUNDED_SHRINKING_SECONDS = 10;

	// TODO: Change default to true as soon as Gradle has support for platform reporter
	// see https://github.com/gradle/gradle/issues/4605
	private static final boolean DEFAULT_USE_JUNIT_PLATFORM_REPORTER = false;

	private final String databasePath;
	private final boolean runFailuresFirst;
	private final int defaultTries;
	private final int defaultMaxDiscardRatio;
	private final boolean useJunitPlatformReporter;
	private final AfterFailureMode defaultAfterFailure;
	private final boolean reportOnlyFailures;
	private final GenerationMode defaultGeneration;
	private final EdgeCasesMode defaultEdgeCases;
	private final ShrinkingMode defaultShrinking;
	private final int boundedShrinkingSeconds;

	public String databasePath() {
		return databasePath;
	}

	public boolean runFailuresFirst() {
		return runFailuresFirst;
	}

	public int defaultTries() {
		return defaultTries;
	}

	public int defaultMaxDiscardRatio() {
		return defaultMaxDiscardRatio;
	}

	public boolean useJunitPlatformReporter() {
		return useJunitPlatformReporter;
	}

	public AfterFailureMode defaultAfterFailure() {
		return defaultAfterFailure;
	}

	public boolean reportOnlyFailures() {
		return reportOnlyFailures;
	}

	public GenerationMode defaultGeneration() {
		return defaultGeneration;
	}

	public EdgeCasesMode defaultEdgeCases() {
		return defaultEdgeCases;
	}

	public ShrinkingMode defaultShrinking() {
		return defaultShrinking;
	}

	public int boundedShrinkingSeconds() {
		return boundedShrinkingSeconds;
	}

	JqwikProperties(ConfigurationParameters configurationParameters) {
		this(configurationParameters, PROPERTIES_FILE_NAME);
	}

	JqwikProperties(ConfigurationParameters parameters, String propertiesFileName) {
		ConfigurationParameters compatibilityParameters = loadCompatibilityProperties(propertiesFileName);

		databasePath = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "database",
				"database",
				Function.identity()
		).orElse(DEFAULT_DATABASE_PATH);

		runFailuresFirst = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "runfailuresfirst",
				"runFailuresFirst",
				Boolean::parseBoolean
		).orElse(DEFAULT_RERUN_FAILURES_FIRST);

		defaultTries = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "tries.default",
				"defaultTries",
				Integer::parseInt
		).orElse(DEFAULT_TRIES);

		defaultMaxDiscardRatio = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "maxdiscardratio.default",
				"defaultMaxDiscardRatio",
				Integer::parseInt
		).orElse(DEFAULT_MAX_DISCARD_RATIO);

		useJunitPlatformReporter = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "usejunitplatformreporter",
				"useJunitPlatformReporter",
				Boolean::parseBoolean
		).orElse(DEFAULT_USE_JUNIT_PLATFORM_REPORTER);

		defaultAfterFailure = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "afterfailure.default",
				"defaultAfterFailure",
				AfterFailureMode::valueOf
		).orElse(DEFAULT_AFTER_FAILURE);

		reportOnlyFailures = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "reportonlyfailures",
				"reportOnlyFailures",
				Boolean::parseBoolean
		).orElse(DEFAULT_REPORT_ONLY_FAILURES);

		defaultGeneration = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "generation.default",
				"defaultGeneration",
				GenerationMode::valueOf
		).orElse(DEFAULT_GENERATION);

		defaultEdgeCases = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "edgecases.default",
				"defaultEdgeCases",
				EdgeCasesMode::valueOf
		).orElse(DEFAULT_EDGE_CASES);

		defaultShrinking = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "shrinking.default",
				"defaultShrinking",
				ShrinkingMode::valueOf
		).orElse(DEFAULT_SHRINKING);

		boundedShrinkingSeconds = property(
				parameters,
				compatibilityParameters,
				CONFIGURATION_PARAMETERS_PREFIX + "boundedshrinkingseconds",
				"boundedShrinkingSeconds",
				Integer::parseInt
		).orElse(DEFAULT_BOUNDED_SHRINKING_SECONDS);
	}

	private static <T> Optional<T> property(
			ConfigurationParameters parameters,
			ConfigurationParameters compatibilityParameters,
			String key,
			String compatibilityKey,
			Function<String, T> converter
	) {
		Optional<T> value = parameters.get(key, converter);
		Optional<T> valueUnderCompatibilityKey =
				key.equals(compatibilityKey)
						? Optional.empty()
						: parameters.get(compatibilityKey, converter);
		Optional<T> compatibilityValue = compatibilityParameters.get(compatibilityKey, converter);

		if (value.isPresent()) {
			complainIfAlsoSpecifiedInCompatibilityParameters(key, compatibilityKey, compatibilityValue);

			return value;
		} else if (valueUnderCompatibilityKey.isPresent()) {
			LOG.log(Level.SEVERE, String.format(
					"Property [%s] is using compatibility key name. Rename to [%s] to eliminate this message.",
					compatibilityKey,
					key
			));

			complainIfAlsoSpecifiedInCompatibilityParameters(compatibilityKey, compatibilityKey, compatibilityValue);

			return valueUnderCompatibilityKey;
		} else if (compatibilityValue.isPresent()) {
			LOG.log(Level.WARNING, String.format(
					"Loaded property [%s] from [%s]. Move to [junit-platform.properties] as [%s] to eliminate this message.",
					compatibilityKey,
					PROPERTIES_FILE_NAME,
					key
			));

			return compatibilityValue;
		}

		return Optional.empty();
	}

	private static <T> void complainIfAlsoSpecifiedInCompatibilityParameters(
			String key,
			String compatibilityKey,
			Optional<T> compatibilityValue
	) {
		if (compatibilityValue.isPresent()) {
			LOG.log(Level.SEVERE, String.format(
					"Loaded property [%s] from JUnit Configuration. Ignoring value for [%s] found in [%s]. Remove to eliminate this message.",
					key,
					compatibilityKey,
					PROPERTIES_FILE_NAME
			));
		}
	}

	private ConfigurationParameters loadCompatibilityProperties(String propertiesFileName) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
		if (inputStream == null) {
			return new JqwikPropertiesConfigurationParameters(new Properties());
		}

		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			warnOnUnsupportedProperties(properties);
			return new JqwikPropertiesConfigurationParameters(properties);
		} catch (Throwable throwable) {
			String message = String.format("Error while reading properties file [%s]", propertiesFileName);
			throw new JqwikException(message, throwable);
		}
	}

	private void warnOnUnsupportedProperties(Properties properties) {
		for (String propertyName : properties.stringPropertyNames()) {
			if (!Arrays.asList(SUPPORTED_PROPERTIES).contains(propertyName)) {
				String message = String.format("Property [%s] is not supported in '%s' file", propertyName, PROPERTIES_FILE_NAME);
				LOG.log(Level.WARNING, message);
			}
		}
	}

	private static class JqwikPropertiesConfigurationParameters implements ConfigurationParameters {
		private final Properties properties;

		private JqwikPropertiesConfigurationParameters(Properties properties) {this.properties = properties;}

		@Override
		public Optional<String> get(String key) {
			return Optional.ofNullable(properties.getProperty(key));
		}

		@Override
		public Optional<Boolean> getBoolean(String key) {
			return get(key, Boolean::parseBoolean);
		}

		@Override
		public int size() {
			return properties.size();
		}
	}

}
