package net.jqwik;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;

class JqwikPropertiesTests {

	private JqwikProperties properties;

	@Example
	void defaultValues() {
		properties = new JqwikProperties("nosuchfile.properties");

		assertThat(properties.runFailuresFirst()).isEqualTo(false);
		assertThat(properties.databasePath()).isEqualTo(".jqwik-database");

		assertThat(properties.defaultTries()).isEqualTo(1000);
		assertThat(properties.defaultMaxDiscardRatio()).isEqualTo(5);

		assertThat(properties.useJunitPlatformReporter()).isEqualTo(false);
	}
}
