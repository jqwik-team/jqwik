package net.jqwik.engine;

import java.io.*;
import java.util.logging.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.config.*;

import net.jqwik.api.*;

public class JqwikProperties {

	public static final int DEFAULT_TRIES = 1000;

	private static final String PROPERTIES_FILE_NAME = "jqwik.properties";
	private static final String CONFIGURATION_PARAMETERS_PREFIX = "jqwik.";
	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private static final String DEFAULT_DATABASE_PATH = ".jqwik-database";
	private static final boolean DEFAULT_RERUN_FAILURES_FIRST = false;
	private static final int DEFAULT_MAX_DISCARD_RATIO = 5;
	private static final AfterFailureMode DEFAULT_AFTER_FAILURE = AfterFailureMode.SAMPLE_FIRST;
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
	private final FixedSeedMode fixedSeedMode;
	private final String defaultSeed;

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

	public FixedSeedMode fixedSeedMode() {
		return fixedSeedMode;
	}

	public String defaultSeed() {
		return defaultSeed;
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
		fixedSeedMode = parameters.get("seeds.whenfixed", FixedSeedMode::valueOf).orElse(FixedSeedMode.ALLOW);
		defaultSeed = parameters.get("seeds.default").orElse(Property.SEED_NOT_SET);
	}

	static JqwikProperties load(ConfigurationParameters fromJunit) {
		severeWarningIfThereIsStillAJqwikPropertiesFile();

		ConfigurationParameters fromJunitPrefixed = new PrefixedConfigurationParameters(fromJunit, CONFIGURATION_PARAMETERS_PREFIX);
		return new JqwikProperties(fromJunitPrefixed);
	}

	private static void severeWarningIfThereIsStillAJqwikPropertiesFile() {
		InputStream legacyProperties = JqwikProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
		if (legacyProperties != null) {
			String message = "Since version 1.6 a jqwik.properties file is no longer supported. Please migrate to junit-platform.properties!";
			LOG.log(Level.SEVERE, message);
		}
	}

}
