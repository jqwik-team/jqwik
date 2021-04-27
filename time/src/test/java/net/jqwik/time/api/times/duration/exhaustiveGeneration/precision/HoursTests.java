package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class HoursTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(HOURS)
				 .between(
					 Duration.ofSeconds(8 * 60 * 60),
					 Duration.ofSeconds(11 * 60 * 60)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(8 * 60 * 60, 0),
			Duration.ofSeconds(9 * 60 * 60, 0),
			Duration.ofSeconds(10 * 60 * 60, 0),
			Duration.ofSeconds(11 * 60 * 60, 0)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(HOURS)
				 .between(
					 Duration.ofSeconds(-11 * 60 * 60),
					 Duration.ofSeconds(-8 * 60 * 60)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-11 * 60 * 60, 0),
			Duration.ofSeconds(-10 * 60 * 60, 0),
			Duration.ofSeconds(-9 * 60 * 60, 0),
			Duration.ofSeconds(-8 * 60 * 60, 0)
		);
	}

	@Example
	void betweenAroundZero() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(HOURS)
				 .between(
					 Duration.ofSeconds(-2 * 60 * 60),
					 Duration.ofSeconds(60 * 60)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-2 * 60 * 60, 0),
			Duration.ofSeconds(-60 * 60, 0),
			Duration.ZERO,
			Duration.ofSeconds(60 * 60, 0)
		);
	}

}
