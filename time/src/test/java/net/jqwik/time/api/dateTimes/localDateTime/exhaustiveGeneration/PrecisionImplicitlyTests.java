package net.jqwik.time.api.dateTimes.localDateTime.exhaustiveGeneration;

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
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 31, 0),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 34, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 31, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 34, 0)
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 57, 0),
							 LocalDateTime.of(2013, 5, 25, 12, 23, 0, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 57, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 58, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 59, 0),
				LocalDateTime.of(2013, 5, 25, 12, 23, 0, 0)
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 0, 0),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 3, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 0, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 1, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 2, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 3, 0)
			);
		}

	}

	@Group
	class Millis {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_000_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 395_000_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 393_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 394_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 395_000_000)
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 32, 997_000_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 997_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 998_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 999_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 0)
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 0),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 3_000_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 0),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 1_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 2_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 3_000_000)
			);
		}

	}

	@Group
	class Micros {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_415_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_413_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_414_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_415_000)
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_997_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 32, 313_000_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_997_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_998_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_999_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 313_000_000)
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_000_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_003_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_000_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_001_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_002_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_003_000)
			);
		}

	}

	@Group
	class Nanos {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_221),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_224)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_221),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_222),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_223),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 392_412_224)
			);
		}

		@Example
		void betweenTheEarliest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_321_997),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_322_000)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_321_997),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_321_998),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_321_999),
				LocalDateTime.of(2013, 5, 25, 12, 22, 32, 312_322_000)
			);
		}

		@Example
		void betweenTheLatest() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
				DateTimes.dateTimes()
						 .between(
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_000),
							 LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_003)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_000),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_001),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_002),
				LocalDateTime.of(2013, 5, 25, 12, 22, 33, 312_542_003)
			);
		}

	}

}
