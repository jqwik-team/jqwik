package net.jqwik.time.api.times.offsetTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class PrecisionTests {

	@Example
	void nanos() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 392_211_325)
				 )
				 .ofPrecision(NANOS)
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, -19, -33), ZoneOffset.ofHoursMinutesSeconds(0, -10, -53))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_211_322), ZoneOffset.ofHoursMinutes(0, -15)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_211_323), ZoneOffset.ofHoursMinutes(0, -15)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_211_324), ZoneOffset.ofHoursMinutes(0, -15)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_211_325), ZoneOffset.ofHoursMinutes(0, -15))
		);
	}

	@Example
	void micros() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 392_214_325)
				 )
				 .ofPrecision(MICROS)
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, 19, 33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_212_000), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_213_000), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 392_214_000), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

	@Example
	void millis() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 33, 395_214_325)
				 )
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, 19, 33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .ofPrecision(MILLIS)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 22, 33, 393_000_000), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 394_000_000), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 33, 395_000_000), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

	@Example
	void seconds() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 22, 36, 395_214_325)
				 )
				 .ofPrecision(SECONDS)
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, 19, 33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 22, 34, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 35, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 22, 36, 0), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

	@Example
	void minutes() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(11, 25, 36, 395_214_325)
				 )
				 .ofPrecision(MINUTES)
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, 19, 33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 23, 0, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 24, 0, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(11, 25, 0, 0), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

	@Example
	void hours() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .ofPrecision(HOURS)
				 .between(
					 LocalTime.of(11, 22, 33, 392_211_322),
					 LocalTime.of(14, 25, 36, 395_214_325)
				 )
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, 19, 33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(12, 0, 0, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(13, 0, 0, 0), ZoneOffset.ofHoursMinutes(0, 30)),
			OffsetTime.of(LocalTime.of(14, 0, 0, 0), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

}
