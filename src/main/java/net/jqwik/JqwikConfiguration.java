package net.jqwik;

public interface JqwikConfiguration {
	PropertyDefaultValues propertyDefaultValues();

	TestEngineConfiguration testEngineConfiguration();

	boolean useJunitPlatformReporter();
}
