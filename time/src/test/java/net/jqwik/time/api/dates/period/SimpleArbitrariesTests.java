package net.jqwik.time.api.dates.period;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<Period> periods() {
		return Dates.periods();
	}

	@Property
	void validPeriodIsGenerated(@ForAll("periods") Period period) {
		assertThat(period).isNotNull();
	}

	@Property
	void defaultYearBetweenMinus1000And1000(@ForAll("periods") Period period) {
		assertThat(period.getYears()).isBetween(-1000, 1000);
	}

	@Property
	void defaultMonthBetweenMinus11And11(@ForAll("periods") Period period) {
		assertThat(period.getMonths()).isBetween(-11, 11);
	}

	@Property
	void defaultDaysBetweenMinus30And30(@ForAll("periods") Period period) {
		assertThat(period.getDays()).isBetween(-30, 30);
	}

}
