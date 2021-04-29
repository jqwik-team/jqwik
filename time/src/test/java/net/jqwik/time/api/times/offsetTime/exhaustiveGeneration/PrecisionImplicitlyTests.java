package net.jqwik.time.api.times.offsetTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class PrecisionImplicitlyTests {

	@Group
	class Seconds {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 31, 0),
						 LocalTime.of(12, 22, 34, 0)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 31, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 34, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 57, 0),
						 LocalTime.of(12, 23, 0, 0)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 57, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 58, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 59, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 23, 0, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 0, 0),
						 LocalTime.of(12, 22, 3, 0)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 1, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 2, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 3, 0), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class Millis {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 392_000_000),
						 LocalTime.of(12, 22, 33, 395_000_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 393_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 394_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 395_000_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 32, 997_000_000),
						 LocalTime.of(12, 22, 33, 0)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 32, 997_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 998_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 999_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 0),
						 LocalTime.of(12, 22, 33, 3_000_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 1_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 2_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 3_000_000), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class Micros {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 392_412_000),
						 LocalTime.of(12, 22, 33, 392_415_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_412_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_413_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_414_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_415_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 32, 312_997_000),
						 LocalTime.of(12, 22, 32, 313_000_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_997_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_998_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_999_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 313_000_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 312_000_000),
						 LocalTime.of(12, 22, 33, 312_003_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_001_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_002_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_003_000), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class Nanos {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 392_412_221),
						 LocalTime.of(12, 22, 33, 392_412_224)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_412_221), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_412_222), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_412_223), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 392_412_224), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 32, 312_321_997),
						 LocalTime.of(12, 22, 32, 312_322_000)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_321_997), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_321_998), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_321_999), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 32, 312_322_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
				Times.offsetTimes()
					 .between(
						 LocalTime.of(12, 22, 33, 312_542_000),
						 LocalTime.of(12, 22, 33, 312_542_003)
					 )
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"))
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_542_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_542_001), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_542_002), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 22, 33, 312_542_003), ZoneOffset.of("Z"))
			);
		}

	}

}
