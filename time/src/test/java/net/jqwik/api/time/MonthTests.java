package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.*;

import static org.assertj.core.api.Assertions.*;

@Group
class MonthTests {

	@Property
	void validMonthIsGenerated(@ForAll("months") Month month) {
		assertThat(month).isNotNull();
	}

	@Provide
	Arbitrary<Month> months() {
		return Dates.months();
	}

	@Group
	class CheckMonthMethods {

		private int startMonth;
		private int endMonth;

		@Property
		void atTheEarliest(@ForAll("monthsAtTheEarliest") Month month) {
			assertThat(month).isGreaterThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(startMonth));
		}

		@Provide
		Arbitrary<Month> monthsAtTheEarliest() {
			startMonth = Arbitraries.integers().between(1, 12).sample();
			return Dates.months().atTheEarliest(startMonth);
		}

		@Property
		void atTheLatest(@ForAll("monthsAtTheLatest") Month month) {
			assertThat(month).isLessThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(endMonth));
		}

		@Provide
		Arbitrary<Month> monthsAtTheLatest() {
			endMonth = Arbitraries.integers().between(1, 12).sample();
			return Dates.months().atTheLatest(endMonth);
		}

		@Property
		void between(@ForAll("monthsBetween") Month month) {
			assertThat(month).isGreaterThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(startMonth));
			assertThat(month).isLessThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(endMonth));
		}

		@Provide
		Arbitrary<Month> monthsBetween() {
			startMonth = Arbitraries.integers().between(1, 12).sample();
			endMonth = Arbitraries.integers().between(startMonth, 12).sample();
			return Dates.months().between(startMonth, endMonth);
		}

		@Property
		void betweenSame(@ForAll("monthsBetweenSame") Month month) {
			assertThat(month).isEqualTo(DefaultMonthArbitrary.getMonthFromInt(startMonth));
		}

		@Provide
		Arbitrary<Month> monthsBetweenSame() {
			startMonth = Arbitraries.integers().between(1, 12).sample();
			endMonth = startMonth;
			return Dates.months().between(startMonth, endMonth);
		}

	}

}
