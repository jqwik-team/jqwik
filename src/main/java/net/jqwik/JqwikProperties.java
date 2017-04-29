package net.jqwik;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.recording.*;

public class JqwikProperties {

	private static final String DEFAULT_PROPERTIES_FILE = "jqwik.properties";
	private static final String JQWIK_DEFAULT_DATABASE = ".jqwik-database";
	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private String databasePath;
	private boolean rerunFailuresWithSameSeed;
	private boolean runFailuresFirst;

	JqwikProperties() {
		this(DEFAULT_PROPERTIES_FILE);
	}

	JqwikProperties(String fileName) {
		loadProperties(fileName);
	}

	public TestEngineConfiguration testEngineConfiguration() {
		TestRunDatabase database = new TestRunDatabase(Paths.get(databasePath));
		TestRunData previousRun = database.previousRun();
		return new TestEngineConfiguration() {
			@Override
			public TestRunRecorder recorder() {
				if (rerunFailuresWithSameSeed || runFailuresFirst)
					return database.recorder();
				return testRun -> {
				};
			}

			@Override
			public TestRunData previousRun() {
				if (!rerunFailuresWithSameSeed)
					return new TestRunData();
				return database.previousRun();
			}

			@Override
			public Set<UniqueId> previousFailures() {
				if (!runFailuresFirst)
					return Collections.emptySet();
				return previousRun.allNonSuccessfulTests().map(testRun -> testRun.getUniqueId()).collect(Collectors.toSet());
			}
		};
	}

	private final void loadProperties(String propertiesFileName) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
		if (inputStream == null) {
			LOG.info(String.format("No Jqwik properties file [%s] found.", propertiesFileName));
			return;
		}

		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			databasePath = properties.getProperty("database", JQWIK_DEFAULT_DATABASE);
			rerunFailuresWithSameSeed = Boolean.parseBoolean(properties.getProperty("rerunFailuresWithSameSeed"));
			runFailuresFirst = Boolean.parseBoolean(properties.getProperty("runFailuresFirst"));
		} catch (IOException ioe) {
			LOG.log(Level.WARNING, String.format("Error while reading properties file [%s] found.", propertiesFileName), ioe);
		}

	}

}
