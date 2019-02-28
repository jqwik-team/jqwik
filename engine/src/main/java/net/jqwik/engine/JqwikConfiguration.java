package net.jqwik.engine;

public interface JqwikConfiguration {
	PropertyDefaultValues propertyDefaultValues();

	TestEngineConfiguration testEngineConfiguration();

	boolean useJunitPlatformReporter();

	boolean reportOnlyFailures();
}
