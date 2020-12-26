package net.jqwik.engine;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class JqwikPropertiesTests {

	private JqwikProperties properties;

	@Example
	@SuppressLogging
	void defaultValues() {
		properties = new JqwikProperties(new ConfigurationParameters() {
			@Override
			public Optional<String> get(String key) {
				return Optional.empty();
			}

			@Override
			public Optional<Boolean> getBoolean(String key) {
				return Optional.empty();
			}

			@Override
			public int size() {
				return 0;
			}
		}, "nosuchfile.properties");

		assertThat(properties.runFailuresFirst()).isEqualTo(false);
		assertThat(properties.databasePath()).isEqualTo(".jqwik-database");

		assertThat(properties.defaultTries()).isEqualTo(1000);
		assertThat(properties.defaultMaxDiscardRatio()).isEqualTo(5);

		assertThat(properties.useJunitPlatformReporter()).isEqualTo(false);

		assertThat(properties.defaultAfterFailure()).isEqualTo(AfterFailureMode.PREVIOUS_SEED);

		assertThat(properties.reportOnlyFailures()).isEqualTo(false);

		assertThat(properties.defaultGeneration()).isEqualTo(GenerationMode.AUTO);

		assertThat(properties.defaultEdgeCases()).isEqualTo(EdgeCasesMode.MIXIN);

		assertThat(properties.defaultShrinking()).isEqualTo(ShrinkingMode.BOUNDED);

		assertThat(properties.boundedShrinkingSeconds()).isEqualTo(10);
	}
}
