package net.jqwik.engine;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
class JqwikPropertiesTests {

	@Example
	void defaultValues() {
		JqwikProperties properties = new JqwikProperties(new ConfigurationParameters() {
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

			@Override
			public Set<String> keySet() {
				return Collections.emptySet();
			}
		});

		assertThat(properties.runFailuresFirst()).isEqualTo(false);
		assertThat(properties.databasePath()).isEqualTo(".jqwik-database");

		assertThat(properties.defaultTries()).isEqualTo(1000);
		assertThat(properties.defaultMaxDiscardRatio()).isEqualTo(5);

		assertThat(properties.useJunitPlatformReporter()).isEqualTo(false);

		assertThat(properties.defaultAfterFailure()).isEqualTo(AfterFailureMode.SAMPLE_FIRST);

		assertThat(properties.reportOnlyFailures()).isEqualTo(false);

		assertThat(properties.defaultGeneration()).isEqualTo(GenerationMode.AUTO);

		assertThat(properties.defaultEdgeCases()).isEqualTo(EdgeCasesMode.MIXIN);

		assertThat(properties.defaultShrinking()).isEqualTo(ShrinkingMode.BOUNDED);

		assertThat(properties.boundedShrinkingSeconds()).isEqualTo(10);

		assertThat(properties.fixedSeedMode()).isEqualTo(FixedSeedMode.ALLOW);
	}
}
