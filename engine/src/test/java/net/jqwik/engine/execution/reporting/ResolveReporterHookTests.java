package net.jqwik.engine.execution.reporting;

import java.util.*;

import com.github.stefanbirkner.systemlambda.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * For this tests to work jqwik.properties must have configuration
 * {@code useJunitPlatformReporter=false}. Otherwise it's not clear what the platform will do.
 */
class ResolveReporterHookTests {

	@Example
	void publishValue(Reporter reporter) throws Exception {
		String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
			reporter.publishValue("mykey", "myvalue");
		});
		assertThat(systemOut).containsSubsequence("mykey", "=", "myvalue");
	}

	@Example
	void publishReports(Reporter reporter) throws Exception {
		String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
			Map<String, Object> reports = new LinkedHashMap<>();
			reports.put("key1", "string1");
			reports.put("key2", 42);
			reporter.publishReports("myreports", reports);
		});
		assertThat(systemOut).containsSubsequence("myreports", "key1: \"string1\"", "key2: 42");
	}

	@Example
	void publishReport(Reporter reporter) throws Exception {
		String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
			reporter.publishReport("aString", "this is a string");
		});
		assertThat(systemOut).containsSubsequence("aString", "\"this is a string\"");
	}

	@Example
	void publishingOrderIsPreserved(Reporter reporter, @ForAll @Size(20) List<@AlphaChars String> list) throws Exception {
		String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
			reporter.publishValue("fortyTwo", Integer.toString(42));
			reporter.publishReport("aString", "this is a string");
			reporter.publishReport("aList", list);
		});
		assertThat(systemOut).containsSubsequence(
			"fortyTwo", "42",
			"aString", "\"this is a string\"",
			"aList", "[", "]"
		);
	}

}
