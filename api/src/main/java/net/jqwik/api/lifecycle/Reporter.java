package net.jqwik.api.lifecycle;

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
@API(status = EXPERIMENTAL, since = "1.2.3")
@FunctionalInterface
public interface Reporter {

	/**
	 * Publish some {@code value} under a given {@code key}.
	 *
	 * @param key   a String
	 * @param value a String
	 */
	void publish(String key, String value);
}
