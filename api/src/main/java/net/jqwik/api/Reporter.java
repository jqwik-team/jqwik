package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An interface that can be used to report additional information for a test element,
 * i.e. a test container or property.
 *
 * <p>
 * Reporting additional test information is a JUnit 5 platform feature.
 * Some tools will output this information to stdout;
 * other tools, e.g. Gradle as of version 6, ignore it.
 * That's why jqwik prints this information to stdout itself
 * unless <a href="https://jqwik.net/docs/current/user-guide.html#jqwik-configuration">told otherwise</a>.
 * </p>
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface Reporter {

	/**
	 * Publish some {@code value} under a given {@code key}.
	 *
	 * @param key   a String
	 * @param value a String
	 */
	void publishValue(String key, String value);

	/**
	 * Publish a report about {@code object} under a given {@code key}.
	 *
	 * <p>
	 * This uses the same mechanism used for jqwik's
	 * <a href="https://jqwik.net/docs/current/user-guide.html#failure-reporting">failure reporting</a>.
	 * </p>
	 *
	 * @param key    a String
	 * @param object any object
	 */
	void publishReport(String key, Object object);

	/**
	 * Publish reports about {@code objects} under a given {@code key}.
	 *
	 * <p>
	 * This uses the same mechanism used for jqwik's
	 * <a href="https://jqwik.net/docs/current/user-guide.html#failure-reporting">failure reporting</a>.
	 * </p>
	 *
	 * @param key    a String
	 * @param objects a map of objects the key of which is used to enumerate them
	 */
	void publishReports(String key, Map<String, Object> objects);
}
