package net.jqwik.time.api.dateTimes.localDateTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

public class BetweenTests {

	@Example
	void timeBetweenSmallerThanBetween() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 10, 22, 31, 0),
						 LocalDateTime.of(2013, 5, 25, 13, 22, 31, 0)
					 )
					 .timeBetween(LocalTime.of(11, 22, 31), LocalTime.of(11, 22, 34))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31),
			LocalDateTime.of(2013, 5, 25, 11, 22, 32),
			LocalDateTime.of(2013, 5, 25, 11, 22, 33),
			LocalDateTime.of(2013, 5, 25, 11, 22, 34)
		);
	}

	@Example
	void betweenSmallerThanTimeBetween() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 0),
						 LocalDateTime.of(2013, 5, 25, 11, 22, 34, 0)
					 )
					 .timeBetween(LocalTime.of(10, 22, 31), LocalTime.of(13, 22, 31))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31),
			LocalDateTime.of(2013, 5, 25, 11, 22, 32),
			LocalDateTime.of(2013, 5, 25, 11, 22, 33),
			LocalDateTime.of(2013, 5, 25, 11, 22, 34)
		);
	}

	@Example
	void hourMinutesSecondsBetweenSmallerThenOther() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 10, 22, 31, 0),
						 LocalDateTime.of(2013, 5, 25, 12, 22, 34, 0)
					 )
					 .timeBetween(LocalTime.of(9, 21, 11), LocalTime.of(13, 25, 31))
					 .hourBetween(11, 11)
					 .minuteBetween(22, 22)
					 .secondBetween(31, 34)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 11, 22, 31),
			LocalDateTime.of(2013, 5, 25, 11, 22, 32),
			LocalDateTime.of(2013, 5, 25, 11, 22, 33),
			LocalDateTime.of(2013, 5, 25, 11, 22, 34)
		);
	}

	@Example
	void dateBetweenSmallerThenBetween() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(1992, 5, 25, 10, 22, 31, 0),
						 LocalDateTime.of(2103, 5, 25, 12, 22, 34, 0)
					 )
					 .timeBetween(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0))
					 .dateBetween(LocalDate.of(2013, 5, 25), LocalDate.of(2013, 5, 28))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 0, 0, 0),
			LocalDateTime.of(2013, 5, 26, 0, 0, 0),
			LocalDateTime.of(2013, 5, 27, 0, 0, 0),
			LocalDateTime.of(2013, 5, 28, 0, 0, 0)
		);
	}

	@Example
	void betweenSmallerThenDateBetween() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 0, 0, 0, 0),
						 LocalDateTime.of(2013, 5, 28, 0, 0, 0, 0)
					 )
					 .timeBetween(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0))
					 .dateBetween(LocalDate.of(1997, 12, 17), LocalDate.of(2210, 5, 28))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 0, 0, 0),
			LocalDateTime.of(2013, 5, 26, 0, 0, 0),
			LocalDateTime.of(2013, 5, 27, 0, 0, 0),
			LocalDateTime.of(2013, 5, 28, 0, 0, 0)
		);
	}

	@Example
	void betweenAndTimeBetweenBeginning() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 24, 10, 22, 31, 0),
						 LocalDateTime.of(2013, 5, 28, 12, 22, 34, 0)
					 )
					 .timeBetween(LocalTime.of(1, 33, 12), LocalTime.of(1, 33, 12))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 1, 33, 12),
			LocalDateTime.of(2013, 5, 26, 1, 33, 12),
			LocalDateTime.of(2013, 5, 27, 1, 33, 12),
			LocalDateTime.of(2013, 5, 28, 1, 33, 12)
		);
	}

	@Example
	void betweenAndTimeBetweenEnd() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .between(
						 LocalDateTime.of(2013, 5, 25, 10, 22, 31, 0),
						 LocalDateTime.of(2013, 5, 29, 12, 22, 34, 0)
					 )
					 .timeBetween(LocalTime.of(15, 33, 12), LocalTime.of(15, 33, 12))
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 5, 25, 15, 33, 12),
			LocalDateTime.of(2013, 5, 26, 15, 33, 12),
			LocalDateTime.of(2013, 5, 27, 15, 33, 12),
			LocalDateTime.of(2013, 5, 28, 15, 33, 12)
		);
	}

	@Example
	void dateBetweenAndBetweenMethods() {
		Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
			DateTimes.dateTimes()
					 .dateBetween(
						 LocalDate.of(2013, 5, 25),
						 LocalDate.of(2014, 11, 29)
					 )
					 .timeBetween(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0))
					 .onlyMonths(AUGUST)
					 .dayOfMonthBetween(11, 12)
					 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalDateTime.of(2013, 8, 11, 0, 0, 0),
			LocalDateTime.of(2013, 8, 12, 0, 0, 0),
			LocalDateTime.of(2014, 8, 11, 0, 0, 0),
			LocalDateTime.of(2014, 8, 12, 0, 0, 0)
		);
	}

}
