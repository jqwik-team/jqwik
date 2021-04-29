package net.jqwik.time.api.dates.monthDay;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void containsAllValues() {
		Optional<ExhaustiveGenerator<MonthDay>> optionalGenerator = Dates.monthDays().exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<MonthDay> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(366); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactlyElementsOf(generateAllMonthDays());
	}

	@Example
	void between() {
		Optional<ExhaustiveGenerator<MonthDay>> optionalGenerator =
			Dates.monthDays()
				 .between(MonthDay.of(Month.FEBRUARY, 27), MonthDay.of(Month.MARCH, 2))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<MonthDay> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			MonthDay.of(Month.FEBRUARY, 27),
			MonthDay.of(Month.FEBRUARY, 28),
			MonthDay.of(Month.FEBRUARY, 29),
			MonthDay.of(Month.MARCH, 1),
			MonthDay.of(Month.MARCH, 2)
		);
	}

	@Example
	void onlyMonthsWithSameDayOfMonths() {
		Optional<ExhaustiveGenerator<MonthDay>> optionalGenerator =
			Dates.monthDays()
				 .dayOfMonthBetween(17, 17)
				 .onlyMonths(Month.APRIL, Month.AUGUST, Month.OCTOBER)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<MonthDay> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(184); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			MonthDay.of(Month.APRIL, 17),
			MonthDay.of(Month.AUGUST, 17),
			MonthDay.of(Month.OCTOBER, 17)
		);
	}

	List<MonthDay> generateAllMonthDays() {
		List<MonthDay> monthDayList = new ArrayList<>();
		for (int m = 1; m <= 12; m++) {
			for (int d = 1; d <= 31; d++) {
				try {
					monthDayList.add(MonthDay.of(m, d));
				} catch (DateTimeException e) {
					//do nothing
				}
			}
		}
		return monthDayList;

	}

}
