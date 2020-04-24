package net.jqwik.engine;

import java.io.*;
import java.util.*;
import java.util.logging.*;

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
		"defaultEdgeCases"
	};

	private static final String PROPERTIES_FILE_NAME = "jqwik.properties";
	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private static final String DEFAULT_DATABASE_PATH = ".jqwik-database";
	private static final String DEFAULT_RERUN_FAILURES_FIRST = "false";
	private static final String DEFAULT_TRIES_VALUE = Integer.toString(DEFAULT_TRIES);
	private static final String DEFAULT_MAX_DISCARD_RATIO = "5";
	private static final String DEFAULT_AFTER_FAILURE = AfterFailureMode.PREVIOUS_SEED.name();
	private static final String DEFAULT_REPORT_ONLY_FAILURES = "false";
	private static final String DEFAULT_GENERATION = GenerationMode.AUTO.name();
	private static final String DEFAULT_EDGE_CASES = EdgeCasesMode.MIXIN.name();

	// TODO: Change default to true as soon as Gradle has support for platform reporter
	// see https://github.com/gradle/gradle/issues/4605
	private static final String DEFAULT_USE_JUNIT_PLATFORM_REPORTER = "false";

	private String databasePath;
	private boolean runFailuresFirst;
	private int defaultTries;
	private int defaultMaxDiscardRatio;
	private boolean useJunitPlatformReporter;
	private AfterFailureMode defaultAfterFailure;
	private boolean reportOnlyFailures;
	private GenerationMode defaultGeneration;
	private EdgeCasesMode defaultEdgeCases;

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

	JqwikProperties() {
		this(PROPERTIES_FILE_NAME);
	}

	JqwikProperties(String fileName) {
		loadProperties(fileName);
	}

	private void loadProperties(String propertiesFileName) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
		if (inputStream == null) {
			LOG.info(String.format("No Jqwik properties file [%s] found.", propertiesFileName));
			inputStream = new ByteArrayInputStream("".getBytes());
		}

		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			warnOnUnsupportedProperties(properties);
			databasePath = properties.getProperty("database", DEFAULT_DATABASE_PATH);
			runFailuresFirst = Boolean.parseBoolean(properties.getProperty("runFailuresFirst", DEFAULT_RERUN_FAILURES_FIRST));
			defaultTries = Integer.parseInt(properties.getProperty("defaultTries", DEFAULT_TRIES_VALUE));
			defaultMaxDiscardRatio = Integer.parseInt(properties.getProperty("defaultMaxDiscardRatio", DEFAULT_MAX_DISCARD_RATIO));
			useJunitPlatformReporter =
				Boolean.parseBoolean(properties.getProperty("useJunitPlatformReporter", DEFAULT_USE_JUNIT_PLATFORM_REPORTER));
			defaultAfterFailure = AfterFailureMode.valueOf(properties.getProperty("defaultAfterFailure", DEFAULT_AFTER_FAILURE));
			reportOnlyFailures = Boolean.parseBoolean(properties.getProperty("reportOnlyFailures", DEFAULT_REPORT_ONLY_FAILURES));
			defaultGeneration = GenerationMode.valueOf(properties.getProperty("defaultGeneration", DEFAULT_GENERATION));
			defaultEdgeCases = EdgeCasesMode.valueOf(properties.getProperty("defaultEdgeCases", DEFAULT_EDGE_CASES));
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

}
