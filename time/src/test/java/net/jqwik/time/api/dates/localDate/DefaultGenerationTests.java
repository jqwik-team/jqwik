package net.jqwik.time.api.dates.localDate;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validDayOfWeekIsGenerated(@ForAll DayOfWeek dayOfWeek) {
		assertThat(dayOfWeek).isNotNull();
	}

	@Property
	void validDayOfMonthIsGenerated(@ForAll @DayOfMonthRange int dayOfMonth) {
		assertThat(dayOfMonth).isBetween(1, 31);
	}

	@Property
	void validDayOfMonthIsGeneratedInteger(@ForAll @DayOfMonthRange Integer dayOfMonth) {
		assertThat(dayOfMonth).isBetween(1, 31);
	}

	@Property
	void validMonthIsGenerated(@ForAll Month month) {
		assertThat(month).isNotNull();
	}

	@Property
	void validLocalDateIsGenerated(@ForAll LocalDate localDate) {
		assertThat(localDate).isNotNull();
	}

}
