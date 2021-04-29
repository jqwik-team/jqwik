package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision.precisionImplicitly;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class MicrosTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 392_412_000),
					 Duration.ofSeconds(123, 392_415_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 392_412_000),
			Duration.ofSeconds(123, 392_413_000),
			Duration.ofSeconds(123, 392_414_000),
			Duration.ofSeconds(123, 392_415_000)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 392_412_000),
					 Duration.ofSeconds(-123, 392_415_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 392_412_000),
			Duration.ofSeconds(-123, 392_413_000),
			Duration.ofSeconds(-123, 392_414_000),
			Duration.ofSeconds(-123, 392_415_000)
		);
	}

	@Example
	void betweenTheEarliestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(122, 312_997_000),
					 Duration.ofSeconds(122, 313_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(122, 312_997_000),
			Duration.ofSeconds(122, 312_998_000),
			Duration.ofSeconds(122, 312_999_000),
			Duration.ofSeconds(122, 313_000_000)
		);
	}

	@Example
	void betweenTheEarliestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-122, 312_997_000),
					 Duration.ofSeconds(-122, 313_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-122, 312_997_000),
			Duration.ofSeconds(-122, 312_998_000),
			Duration.ofSeconds(-122, 312_999_000),
			Duration.ofSeconds(-122, 313_000_000)
		);
	}

	@Example
	void betweenTheLatestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 312_000_000),
					 Duration.ofSeconds(123, 312_003_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 312_000_000),
			Duration.ofSeconds(123, 312_001_000),
			Duration.ofSeconds(123, 312_002_000),
			Duration.ofSeconds(123, 312_003_000)
		);
	}

	@Example
	void betweenTheLatestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 312_000_000),
					 Duration.ofSeconds(-123, 312_003_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 312_000_000),
			Duration.ofSeconds(-123, 312_001_000),
			Duration.ofSeconds(-123, 312_002_000),
			Duration.ofSeconds(-123, 312_003_000)
		);
	}

}
