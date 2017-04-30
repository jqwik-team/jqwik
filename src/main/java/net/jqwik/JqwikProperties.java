package net.jqwik;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class JqwikProperties {

	private static final String DEFAULT_PROPERTIES_FILE = "jqwik.properties";
	private static final String JQWIK_DEFAULT_DATABASE = ".jqwik-database";
	private static final Logger LOG = Logger.getLogger(JqwikProperties.class.getName());

	private String databasePath;
	private boolean rerunFailuresWithSameSeed;

	public String getDatabasePath() {
		return databasePath;
	}

	public boolean rerunFailuresWithSameSeed() {
		return rerunFailuresWithSameSeed;
	}

	public boolean runFailuresFirst() {
		return runFailuresFirst;
	}

	private boolean runFailuresFirst;

	JqwikProperties() {
		this(DEFAULT_PROPERTIES_FILE);
	}

	JqwikProperties(String fileName) {
		loadProperties(fileName);
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
