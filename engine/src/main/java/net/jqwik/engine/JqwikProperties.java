package net.jqwik.engine;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.config.*;

import net.jqwik.api.*;

public class JqwikProperties {

	public static final int DEFAULT_TRIES = 1000;

	private static final Map<String, String> COMPATIBILITY_PROPERTY_NAMES;

	static {
		Map<String, String> priorNames = new HashMap<>();
		priorNames.put("database", "database");
		priorNames.put("failures.runfirst", "runFailuresFirst");
		priorNames.put("tries.default", "defaultTries");
		priorNames.put("maxdiscardratio.default", "defaultMaxDiscardRatio");
		priorNames.put("reporting.usejunitplatform", "useJunitPlatformReporter");
		priorNames.put("failures.after.default", "defaultAfterFailure");
		priorNames.put("reporting.onlyfailures", "reportOnlyFailures");
		priorNames.put("generation.default", "defaultGeneration");
		priorNames.put("edgecases.default", "defaultEdgeCases");
		priorNames.put("shrinking.default", "defaultShrinking");
		priorNames.put("shrinking.bounded.seconds", "boundedShrinkingSeconds");
		COMPATIBILITY_PROPERTY_NAMES = Collections.unmodifiableMap(priorNames);
	}

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

	JqwikProperties(ConfigurationParameters parameters) {
		databasePath = parameters.get("database").orElse(DEFAULT_DATABASE_PATH);
		runFailuresFirst = parameters.getBoolean("failures.runfirst").orElse(DEFAULT_RERUN_FAILURES_FIRST);
		defaultTries = parameters.get("tries.default", Integer::parseInt).orElse(DEFAULT_TRIES);
		defaultMaxDiscardRatio = parameters.get("maxdiscardratio.default", Integer::parseInt).orElse(DEFAULT_MAX_DISCARD_RATIO);
		useJunitPlatformReporter = parameters.getBoolean("reporting.usejunitplatform").orElse(DEFAULT_USE_JUNIT_PLATFORM_REPORTER);
		defaultAfterFailure = parameters.get("failures.after.default", AfterFailureMode::valueOf).orElse(DEFAULT_AFTER_FAILURE);
		reportOnlyFailures = parameters.getBoolean("reporting.onlyfailures").orElse(DEFAULT_REPORT_ONLY_FAILURES);
		defaultGeneration = parameters.get("generation.default", GenerationMode::valueOf).orElse(DEFAULT_GENERATION);
		defaultEdgeCases = parameters.get("edgecases.default", EdgeCasesMode::valueOf).orElse(DEFAULT_EDGE_CASES);
		defaultShrinking = parameters.get("shrinking.default", ShrinkingMode::valueOf).orElse(DEFAULT_SHRINKING);
		boundedShrinkingSeconds = parameters.get("shrinking.bounded.seconds", Integer::parseInt).orElse(DEFAULT_BOUNDED_SHRINKING_SECONDS);
	}

	static JqwikProperties loadWithBackwardsCompatibility(ConfigurationParameters fromJunit) {
		// When backwards compatibility is eliminated, `fromJunitPrefix` can be passed directly to the constructor
		ConfigurationParameters fromJunitPrefixed = new PrefixedConfigurationParameters(fromJunit, CONFIGURATION_PARAMETERS_PREFIX);
		ConfigurationParameters fromJqwikProperties = new JqwikPropertiesFileConfigurationParameters();
		return new JqwikProperties(new CompatibilityConfigurationParameters(fromJunitPrefixed, fromJunit, fromJqwikProperties));
	}

	private static class CompatibilityConfigurationParameters implements ConfigurationParameters {
		private final ConfigurationParameters fromJunitPrefixed;
		private final ConfigurationParameters fromJunitUnPrefixed;
		private final ConfigurationParameters fromJqwikProperties;

		private CompatibilityConfigurationParameters(
				ConfigurationParameters fromJunitPrefixed, ConfigurationParameters fromJunit, ConfigurationParameters fromJqwikProperties
		) {
			this.fromJunitPrefixed = fromJunitPrefixed;
			this.fromJunitUnPrefixed = fromJunit;
			this.fromJqwikProperties = fromJqwikProperties;
		}

		@Override
		public Optional<String> get(String key) {
			return get(key, Function.identity());
		}

		@Override
		public Optional<Boolean> getBoolean(String key) {
			return get(key, Boolean::parseBoolean);
		}

		@Override
		public int size() {
			// we never call this. due to the compatibility logic, what's the "right" answer?
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> Optional<T> get(String key, Function<String, T> transformer) {
			Optional<T> value = fromJunitPrefixed.get(key, transformer);
			Optional<String> priorName = Optional.ofNullable(COMPATIBILITY_PROPERTY_NAMES.get(key));

			if (value.isPresent()) {
				priorName.ifPresent(compatibilityKey -> complainIfAlsoSpecifiedInCompatibilityParameters(key, compatibilityKey));

				return value;
			}

			return priorName.flatMap(compatibilityKey -> compatibilityGet(key, compatibilityKey, transformer));
		}

		private <T> Optional<T> compatibilityGet(String key, String compatibilityKey, Function<String, T> transformer) {
			Optional<T> valueUnderCompatibilityKey = fromJunitUnPrefixed.get(compatibilityKey, transformer);

			if (valueUnderCompatibilityKey.isPresent()) {
				LOG.log(Level.SEVERE, String.format(
						"Property [%s] is using compatibility key name. Rename to [%s] to eliminate this message.",
						compatibilityKey,
						key
				));

				complainIfAlsoSpecifiedInCompatibilityParameters(compatibilityKey, compatibilityKey);

				return valueUnderCompatibilityKey;
			}

			Optional<T> compatibilityValue = fromJqwikProperties.get(compatibilityKey, transformer);

			if (compatibilityValue.isPresent()) {
				LOG.log(Level.WARNING, String.format(
						"Loaded property [%s] from [%s]. Move to [junit-platform.properties] as [%s] to eliminate this message.",
						compatibilityKey,
						PROPERTIES_FILE_NAME,
						key
				));
			}

			return compatibilityValue;
		}

		private void complainIfAlsoSpecifiedInCompatibilityParameters(String key, String compatibilityKey) {
			fromJqwikProperties.get(compatibilityKey)
							   .ifPresent(v -> LOG.log(Level.SEVERE, String.format(
									   "Loaded property [%s] from JUnit Configuration. Ignoring value for [%s] found in [%s]. Remove to eliminate this message.",
									   key,
									   compatibilityKey,
									   PROPERTIES_FILE_NAME
							   )));
		}
	}

	private static class JqwikPropertiesFileConfigurationParameters implements ConfigurationParameters {
		private final Properties properties = new Properties();

		JqwikPropertiesFileConfigurationParameters() {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

			if (inputStream != null) {
				try {
					properties.load(inputStream);
				} catch (Throwable throwable) {
					String message = String.format("Error while reading properties file [%s]", PROPERTIES_FILE_NAME);
					throw new JqwikException(message, throwable);
				}

				warnOnUnsupportedProperties();
			}
		}

		private void warnOnUnsupportedProperties() {
			for (String propertyName : properties.stringPropertyNames()) {
				if (!COMPATIBILITY_PROPERTY_NAMES.containsValue(propertyName)) {
					String message = String.format("Property [%s] is not supported in '%s' file", propertyName, PROPERTIES_FILE_NAME);
					LOG.log(Level.WARNING, message);
				}
			}
		}

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
