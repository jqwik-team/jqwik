package net.jqwik.time.api.dates.yearMonth;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Example
	void minYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.yearMonths().yearBetween(0, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.yearMonths().yearBetween(-1000, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.yearMonths().yearBetween(2000, 0)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.yearMonths().yearBetween(2000, -1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheEarliestYearMustNotBeBelow1(@ForAll @IntRange(min = -999_999_999, max = 0) int year, @ForAll Month month) {
		assertThatThrownBy(
			() -> Dates.yearMonths().atTheEarliest(YearMonth.of(year, month))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheLatestYearMustNotBeBelow1(@ForAll @IntRange(min = -999_999_999, max = 0) int year, @ForAll Month month) {
		assertThatThrownBy(
			() -> Dates.yearMonths().atTheLatest(YearMonth.of(year, month))
		).isInstanceOf(IllegalArgumentException.class);
	}

}
