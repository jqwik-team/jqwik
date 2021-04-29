package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision.precisionImplicitly;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class MillisTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 392_000_000),
					 Duration.ofSeconds(123, 395_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 392_000_000),
			Duration.ofSeconds(123, 393_000_000),
			Duration.ofSeconds(123, 394_000_000),
			Duration.ofSeconds(123, 395_000_000)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 392_000_000),
					 Duration.ofSeconds(-123, 395_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 392_000_000),
			Duration.ofSeconds(-123, 393_000_000),
			Duration.ofSeconds(-123, 394_000_000),
			Duration.ofSeconds(-123, 395_000_000)
		);
	}

	@Example
	void betweenTheEarliestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(122, 997_000_000),
					 Duration.ofSeconds(123, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(122, 997_000_000),
			Duration.ofSeconds(122, 998_000_000),
			Duration.ofSeconds(122, 999_000_000),
			Duration.ofSeconds(123, 0)
		);
	}

	@Example
	void betweenTheEarliestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-122, 997_000_000),
					 Duration.ofSeconds(-121, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-122, 997_000_000),
			Duration.ofSeconds(-122, 998_000_000),
			Duration.ofSeconds(-122, 999_000_000),
			Duration.ofSeconds(-121, 0)
		);
	}

	@Example
	void betweenTheLatestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 0),
					 Duration.ofSeconds(123, 3_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 0),
			Duration.ofSeconds(123, 1_000_000),
			Duration.ofSeconds(123, 2_000_000),
			Duration.ofSeconds(123, 3_000_000)
		);
	}

	@Example
	void betweenTheLatestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 0),
					 Duration.ofSeconds(-123, 3_000_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 0),
			Duration.ofSeconds(-123, 1_000_000),
			Duration.ofSeconds(-123, 2_000_000),
			Duration.ofSeconds(-123, 3_000_000)
		);
	}

}
