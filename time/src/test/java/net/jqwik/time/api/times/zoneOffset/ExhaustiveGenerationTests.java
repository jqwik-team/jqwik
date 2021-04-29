package net.jqwik.time.api.times.zoneOffset;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void betweenPositive() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(11, 11, 23),
					 ZoneOffset.ofHoursMinutesSeconds(12, 19, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(11, 15, 0),
			ZoneOffset.ofHoursMinutesSeconds(11, 30, 0),
			ZoneOffset.ofHoursMinutesSeconds(11, 45, 0),
			ZoneOffset.ofHoursMinutesSeconds(12, 0, 0),
			ZoneOffset.ofHoursMinutesSeconds(12, 15, 0)
		);
	}

	@Example
	void betweenPositiveIncreaseMinutesWithSeconds() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(11, 59, 59),
					 ZoneOffset.ofHoursMinutesSeconds(12, 19, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(12, 0, 0),
			ZoneOffset.ofHoursMinutesSeconds(12, 15, 0)
		);
	}

	@Example
	void betweenPositiveIncreaseMinutes() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(11, 58, 59),
					 ZoneOffset.ofHoursMinutesSeconds(12, 19, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(12, 0, 0),
			ZoneOffset.ofHoursMinutesSeconds(12, 15, 0)
		);
	}

	@Example
	void betweenNegative() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(-9, -14, -59),
					 ZoneOffset.ofHoursMinutesSeconds(-8, -15, -11)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0),
			ZoneOffset.ofHoursMinutesSeconds(-8, -45, 0),
			ZoneOffset.ofHoursMinutesSeconds(-8, -30, 0)
		);
	}

	@Example
	void betweenNegativeDecreaseMinutesWithSeconds() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(-9, -23, -59),
					 ZoneOffset.ofHoursMinutesSeconds(-8, -59, -59)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(-9, -15, 0),
			ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0)
		);
	}

	@Example
	void betweenNegativeDecreaseMinutes() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(-9, -23, -59),
					 ZoneOffset.ofHoursMinutesSeconds(-8, -58, -59)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(2);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(-9, -15, 0),
			ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0)
		);
	}

	@Example
	void sameOffsetNegative() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0),
					 ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(1);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0)
		);
	}

	@Example
	void sameOffsetPositive() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(14, 0, 0),
					 ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(1);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
		);
	}

	@Example
	void sameOffsetZero() {
		Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
			Times.zoneOffsets()
				 .between(
					 ZoneOffset.ofHoursMinutesSeconds(0, 0, 0),
					 ZoneOffset.ofHoursMinutesSeconds(0, 0, 0)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(1);
		assertThat(generator).containsExactly(
			ZoneOffset.ofHoursMinutesSeconds(0, 0, 0)
		);
	}

}
