package net.jqwik.time.api.dates.yearMonth;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<YearMonth>> optionalGenerator =
			Dates.yearMonths()
				 .between(YearMonth.of(41, Month.OCTOBER), YearMonth.of(42, Month.FEBRUARY))
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<YearMonth> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(5); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			YearMonth.of(41, Month.OCTOBER),
			YearMonth.of(41, Month.NOVEMBER),
			YearMonth.of(41, Month.DECEMBER),
			YearMonth.of(42, Month.JANUARY),
			YearMonth.of(42, Month.FEBRUARY)
		);
	}

	@Example
	void onlyMonthsWithSameYear() {
		Optional<ExhaustiveGenerator<YearMonth>> optionalGenerator = Dates.yearMonths().yearBetween(42, 42)
																		  .onlyMonths(Month.FEBRUARY, Month.MARCH, Month.SEPTEMBER)
																		  .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<YearMonth> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(12); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			YearMonth.of(42, Month.FEBRUARY),
			YearMonth.of(42, Month.MARCH),
			YearMonth.of(42, Month.SEPTEMBER)
		);
	}

}
