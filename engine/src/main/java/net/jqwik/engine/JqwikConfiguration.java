package net.jqwik.engine;

public interface JqwikConfiguration {
	PropertyAttributesDefaults propertyDefaultValues();

	TestEngineConfiguration testEngineConfiguration();

	boolean useJunitPlatformReporter();

	boolean reportOnlyFailures();
}
