package net.jqwik.time.api.dateTimes.localDateTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class PrecisionTests {

	@Example
	void nanos() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_322),
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_325)
					 )
					 .ofPrecision(NANOS)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_322),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_323),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_324),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_325)
		);
	}

	@Example
	void micros() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_212_000),
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_215_000)
					 )
					 .ofPrecision(MICROS)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_212_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_213_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_214_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_215_000)
		);
	}

	@Example
	void millis() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 393_000_000),
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 396_000_000)
					 )
					 .ofPrecision(MILLIS)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 393_000_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 394_000_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 395_000_000),
			LocalDateTime.of(2013, 5, 25, 11, 22, 31, 396_000_000)
		);
	}

	@Example
	void seconds() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 22, 32),
						 LocalDateTime.of(2013, 5, 25, 11, 22, 35)
					 )
					 .ofPrecision(SECONDS)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 32),
			LocalDateTime.of(2013, 5, 25, 11, 22, 33),
			LocalDateTime.of(2013, 5, 25, 11, 22, 34),
			LocalDateTime.of(2013, 5, 25, 11, 22, 35)
		);
	}

	@Example
	void minutes() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 23, 0),
						 LocalDateTime.of(2013, 5, 25, 11, 26, 0)
					 )
					 .ofPrecision(MINUTES)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 23, 0),
			LocalDateTime.of(2013, 5, 25, 11, 24, 0),
			LocalDateTime.of(2013, 5, 25, 11, 25, 0),
			LocalDateTime.of(2013, 5, 25, 11, 26, 0)
		);
	}

	@Example
	void hours() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 12, 0, 0),
						 LocalDateTime.of(2013, 5, 25, 15, 0, 0)
					 )
					 .ofPrecision(HOURS)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 12, 0, 0),
			LocalDateTime.of(2013, 5, 25, 13, 0, 0),
			LocalDateTime.of(2013, 5, 25, 14, 0, 0),
			LocalDateTime.of(2013, 5, 25, 15, 0, 0)
		);
	}

}
