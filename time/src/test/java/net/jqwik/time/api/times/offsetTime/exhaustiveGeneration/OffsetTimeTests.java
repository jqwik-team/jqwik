package net.jqwik.time.api.times.offsetTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class OffsetTimeTests {

	@Example
	void offsetBetween() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33),
					 LocalTime.of(11, 22, 33)
				 )
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, -19, -33), ZoneOffset.ofHoursMinutesSeconds(0, 31, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, -15)),
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, 0)),
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, 15)),
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, 30))
		);
	}

	@Example
	void betweenMethods() {
		Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
			Times.offsetTimes()
				 .between(
					 LocalTime.of(11, 22, 33),
					 LocalTime.of(11, 22, 34)
				 )
				 .offsetBetween(ZoneOffset.ofHoursMinutesSeconds(0, -1, -33), ZoneOffset.ofHoursMinutesSeconds(0, 29, 11))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactlyInAnyOrder(
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, 0)),
			OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHoursMinutes(0, 15)),
			OffsetTime.of(LocalTime.of(11, 22, 34), ZoneOffset.ofHoursMinutes(0, 0)),
			OffsetTime.of(LocalTime.of(11, 22, 34), ZoneOffset.ofHoursMinutes(0, 15))
		);
	}

}
