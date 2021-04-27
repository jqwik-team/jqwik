package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class SecondsTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(SECONDS)
				 .between(
					 Duration.ofSeconds(183727),
					 Duration.ofSeconds(183730)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(183727, 0),
			Duration.ofSeconds(183728, 0),
			Duration.ofSeconds(183729, 0),
			Duration.ofSeconds(183730, 0)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(SECONDS)
				 .between(
					 Duration.ofSeconds(-183729),
					 Duration.ofSeconds(-183726)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-183729, 0),
			Duration.ofSeconds(-183728, 0),
			Duration.ofSeconds(-183727, 0),
			Duration.ofSeconds(-183726, 0)
		);
	}

	@Example
	void betweenAroundZero() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .ofPrecision(SECONDS)
				 .between(
					 Duration.ofSeconds(-2),
					 Duration.ofSeconds(1)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-2, 0),
			Duration.ofSeconds(-1, 0),
			Duration.ZERO,
			Duration.ofSeconds(1, 0)
		);
	}

}
