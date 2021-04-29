package net.jqwik.time.api.dates.localDate;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
	}

	@Property
	void validDayOfWeekIsGenerated(@ForAll("daysOfWeek") DayOfWeek dayOfWeek) {
		assertThat(dayOfWeek).isNotNull();
	}

	@Provide
	Arbitrary<DayOfWeek> daysOfWeek() {
		return Dates.daysOfWeek();
	}

	@Property
	void validDayOfMonthIsGenerated(@ForAll("daysOfMonth") int dayOfMonth) {
		assertThat(dayOfMonth).isBetween(1, 31);
	}

	@Provide
	Arbitrary<Integer> daysOfMonth() {
		return Dates.daysOfMonth();
	}

	@Property
	void validMonthIsGenerated(@ForAll("months") Month month) {
		assertThat(month).isNotNull();
	}

	@Provide
	Arbitrary<Month> months() {
		return Dates.months();
	}

	@Property
	void validLocalDateIsGenerated(@ForAll("dates") LocalDate localDate) {
		assertThat(localDate).isNotNull();
	}

}
