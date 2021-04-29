package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class MicrosTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(MICROS)
				 .between(
					 Duration.ofSeconds(183729, 999_997_921),
					 Duration.ofSeconds(183730, 1_213)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(183729, 999_998_000),
			Duration.ofSeconds(183729, 999_999_000),
			Duration.ofSeconds(183730, 0),
			Duration.ofSeconds(183730, 1_000)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(MICROS)
				 .between(
					 Duration.ofSeconds(-183730, 999_997_998),
					 Duration.ofSeconds(-183729, 1_999)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-183730, 999_998_000),
			Duration.ofSeconds(-183730, 999_999_000),
			Duration.ofSeconds(-183729, 0),
			Duration.ofSeconds(-183729, 1_000)
		);
	}

	@Example
	void betweenNegativeOneSecond() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(MICROS)
				 .between(
					 Duration.ofSeconds(-1, -1_302),
					 Duration.ofSeconds(0, -999_997_323)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-1, -1_000),
			Duration.ofSeconds(-1, 0),
			Duration.ofSeconds(0, -999_999_000),
			Duration.ofSeconds(0, -999_998_000)
		);
	}

	@Example
	void betweenAroundZero() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(MICROS)
				 .between(
					 Duration.ofSeconds(0, -2_321),
					 Duration.ofSeconds(0, 1_392)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(0, -2_000),
			Duration.ofSeconds(0, -1_000),
			Duration.ZERO,
			Duration.ofSeconds(0, 1_000)
		);
	}

	@Example
	void betweenPositiveOneSecond() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(MICROS)
				 .between(
					 Duration.ofSeconds(0, 999_997_213),
					 Duration.ofSeconds(1, 1_023)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(0, 999_998_000),
			Duration.ofSeconds(0, 999_999_000),
			Duration.ofSeconds(1, 0),
			Duration.ofSeconds(1, 1_000)
		);
	}

}
