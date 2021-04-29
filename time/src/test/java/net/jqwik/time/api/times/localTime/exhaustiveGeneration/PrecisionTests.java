package net.jqwik.time.api.times.localTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class PrecisionTests {

	@Example
	void nanos() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 392_211_325)
				 )
				 .ofPrecision(NANOS)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 22, 33, 392_211_322),
			LocalTime.of(11, 22, 33, 392_211_323),
			LocalTime.of(11, 22, 33, 392_211_324),
			LocalTime.of(11, 22, 33, 392_211_325)
		);
	}

	@Example
	void micros() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 392_214_325)
				 )
				 .ofPrecision(MICROS)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 22, 33, 392_212_000),
			LocalTime.of(11, 22, 33, 392_213_000),
			LocalTime.of(11, 22, 33, 392_214_000)
		);
	}

	@Example
	void millis() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .ofPrecision(MILLIS)
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 395_214_325)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 22, 33, 393_000_000),
			LocalTime.of(11, 22, 33, 394_000_000),
			LocalTime.of(11, 22, 33, 395_000_000)
		);
	}

	@Example
	void seconds() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .ofPrecision(SECONDS)
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 36, 395_214_325)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 22, 34, 0),
			LocalTime.of(11, 22, 35, 0),
			LocalTime.of(11, 22, 36, 0)
		);
	}

	@Example
	void minutes() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 25, 36, 395_214_325)
				 )
				 .ofPrecision(MINUTES)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 23, 0, 0),
			LocalTime.of(11, 24, 0, 0),
			LocalTime.of(11, 25, 0, 0)
		);
	}

	@Example
	void hours() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(14, 25, 36, 395_214_325)
				 )
				 .ofPrecision(HOURS)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			LocalTime.of(12, 0, 0, 0),
			LocalTime.of(13, 0, 0, 0),
			LocalTime.of(14, 0, 0, 0)
		);
	}

}
