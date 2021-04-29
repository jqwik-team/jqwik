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
					 Duration.ofSeconds(183726, 997_997_921),
					 Duration.ofSeconds(183730, 1_213_999)
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
					 Duration.ofSeconds(-183730, 997_123_998),
					 Duration.ofSeconds(-183726, 1_999_999)
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
					 Duration.ofSeconds(-2, -2_321_392),
					 Duration.ofSeconds(1, 1_392_392)
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
