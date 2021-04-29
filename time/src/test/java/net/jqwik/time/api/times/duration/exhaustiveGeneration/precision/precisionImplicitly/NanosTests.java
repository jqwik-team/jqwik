package net.jqwik.time.api.times.duration.exhaustiveGeneration.precision.precisionImplicitly;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class NanosTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 392_412_221),
					 Duration.ofSeconds(123, 392_412_224)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 392_412_221),
			Duration.ofSeconds(123, 392_412_222),
			Duration.ofSeconds(123, 392_412_223),
			Duration.ofSeconds(123, 392_412_224)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 392_412_221),
					 Duration.ofSeconds(-123, 392_412_224)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 392_412_221),
			Duration.ofSeconds(-123, 392_412_222),
			Duration.ofSeconds(-123, 392_412_223),
			Duration.ofSeconds(-123, 392_412_224)
		);
	}

	@Example
	void betweenTheEarliestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(122, 312_321_997),
					 Duration.ofSeconds(122, 312_322_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(122, 312_321_997),
			Duration.ofSeconds(122, 312_321_998),
			Duration.ofSeconds(122, 312_321_999),
			Duration.ofSeconds(122, 312_322_000)
		);
	}

	@Example
	void betweenTheEarliestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-122, 312_321_997),
					 Duration.ofSeconds(-122, 312_322_000)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-122, 312_321_997),
			Duration.ofSeconds(-122, 312_321_998),
			Duration.ofSeconds(-122, 312_321_999),
			Duration.ofSeconds(-122, 312_322_000)
		);
	}

	@Example
	void betweenTheLatestPositive() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(123, 312_542_000),
					 Duration.ofSeconds(123, 312_542_003)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(123, 312_542_000),
			Duration.ofSeconds(123, 312_542_001),
			Duration.ofSeconds(123, 312_542_002),
			Duration.ofSeconds(123, 312_542_003)
		);
	}

	@Example
	void betweenTheLatestNegative() {
		Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
			Times.durations()
				 .between(
					 Duration.ofSeconds(-123, 312_542_000),
					 Duration.ofSeconds(-123, 312_542_003)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			Duration.ofSeconds(-123, 312_542_000),
			Duration.ofSeconds(-123, 312_542_001),
			Duration.ofSeconds(-123, 312_542_002),
			Duration.ofSeconds(-123, 312_542_003)
		);
	}

}
