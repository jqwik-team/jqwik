package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class NanosTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(NANOS)
				 .between(
					 Duration.ofSeconds(183729, 999_999_998),
					 Duration.ofSeconds(183730, 1)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(183729, 999_999_998),
			Duration.ofSeconds(183729, 999_999_999),
			Duration.ofSeconds(183730, 0),
			Duration.ofSeconds(183730, 1)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(NANOS)
				 .between(
					 Duration.ofSeconds(-183730, 999_999_998),
					 Duration.ofSeconds(-183729, 1)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-183730, 999_999_998),
			Duration.ofSeconds(-183730, 999_999_999),
			Duration.ofSeconds(-183729, 0),
			Duration.ofSeconds(-183729, 1)
		);
	}

	@Example
	void betweenNegativeOneSecond() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(NANOS)
				 .between(
					 Duration.ofSeconds(-1, -1),
					 Duration.ofSeconds(0, -999_999_998)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-1, -1),
			Duration.ofSeconds(-1, 0),
			Duration.ofSeconds(0, -999_999_999),
			Duration.ofSeconds(0, -999_999_998)
		);
	}

	@Example
	void betweenAroundZero() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(NANOS)
				 .between(
					 Duration.ofSeconds(0, -2),
					 Duration.ofSeconds(0, 1)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(0, -2),
			Duration.ofSeconds(0, -1),
			Duration.ZERO,
			Duration.ofSeconds(0, 1)
		);
	}

	@Example
	void betweenPositiveOneSecond() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(NANOS)
				 .between(
					 Duration.ofSeconds(0, 999_999_998),
					 Duration.ofSeconds(1, 1)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(0, 999_999_998),
			Duration.ofSeconds(0, 999_999_999),
			Duration.ofSeconds(1, 0),
			Duration.ofSeconds(1, 1)
		);
	}

}
