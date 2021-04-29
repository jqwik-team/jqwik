package net.jqwik.time.api.times.localTime.exhaustiveGeneration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class BetweenTests {

	@Example
	void betweenInBetweens() {

		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(11, 11, 11),
					 LocalTime.of(11, 11, 14)
				 )
				 .hourBetween(5, 15)
				 .minuteBetween(3, 33)
				 .secondBetween(9, 38)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 11, 11),
			LocalTime.of(11, 11, 12),
			LocalTime.of(11, 11, 13),
			LocalTime.of(11, 11, 14)
		);
	}

	@Example
	void hourBetweenAndBetween() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(5, 11, 11),
					 LocalTime.of(12, 24, 21)
				 )
				 .hourBetween(6, 9)
				 .minuteBetween(3, 3)
				 .secondBetween(9, 9)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3 * 60 * 60 + 1);
		assertThat(generator).containsExactly(
			LocalTime.of(6, 3, 9),
			LocalTime.of(7, 3, 9),
			LocalTime.of(8, 3, 9),
			LocalTime.of(9, 3, 9)
		);
	}

	@Example
	void minuteBetweenAndBetweenHigh() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(9, 11, 11),
					 LocalTime.of(12, 24, 21)
				 )
				 .hourBetween(11, 11)
				 .minuteBetween(17, 20)
				 .secondBetween(18, 18)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(3 * 60 + 1);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 17, 18),
			LocalTime.of(11, 18, 18),
			LocalTime.of(11, 19, 18),
			LocalTime.of(11, 20, 18)
		);
	}

	@Example
	void secondBetweenAndBetweenLow() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(9, 21, 11),
					 LocalTime.of(12, 24, 21)
				 )
				 .hourBetween(11, 11)
				 .minuteBetween(19, 19)
				 .secondBetween(5, 8)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 19, 5),
			LocalTime.of(11, 19, 6),
			LocalTime.of(11, 19, 7),
			LocalTime.of(11, 19, 8)
		);
	}

	@Example
	void secondBetweenAndBetween() {
		Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
			Times.times()
				 .between(
					 LocalTime.of(9, 21, 11),
					 LocalTime.of(12, 24, 21)
				 )
				 .hourBetween(11, 11)
				 .minuteBetween(37, 37)
				 .secondBetween(30, 33)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4);
		assertThat(generator).containsExactly(
			LocalTime.of(11, 37, 30),
			LocalTime.of(11, 37, 31),
			LocalTime.of(11, 37, 32),
			LocalTime.of(11, 37, 33)
		);
	}

}
