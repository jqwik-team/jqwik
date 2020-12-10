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

		private Month startMonth;
		private Month endMonth;

		@Property
		void atTheEarliest(@ForAll("monthsAtTheEarliest") Month month) {
			assertThat(month).isGreaterThanOrEqualTo(startMonth);
		}

		@Provide
		Arbitrary<Month> monthsAtTheEarliest() {
			startMonth = DefaultMonthArbitrary.getMonthFromInt(Arbitraries.integers().between(1, 12).sample());
			return Dates.months().atTheEarliest(startMonth);
		}

		@Property
		void atTheLatest(@ForAll("monthsAtTheLatest") Month month) {
			assertThat(month).isLessThanOrEqualTo(endMonth);
		}

		@Provide
		Arbitrary<Month> monthsAtTheLatest() {
			endMonth = DefaultMonthArbitrary.getMonthFromInt(Arbitraries.integers().between(1, 12).sample());
			return Dates.months().atTheLatest(endMonth);
		}

		@Property
		void between(@ForAll("monthsBetween") Month month) {
			assertThat(month).isGreaterThanOrEqualTo(startMonth);
			assertThat(month).isLessThanOrEqualTo(endMonth);
		}

		@Provide
		Arbitrary<Month> monthsBetween() {
			int start = Arbitraries.integers().between(1, 12).sample();
			startMonth = DefaultMonthArbitrary.getMonthFromInt(start);
			endMonth = DefaultMonthArbitrary.getMonthFromInt(Arbitraries.integers().between(start, 12).sample());
			return Dates.months().between(startMonth, endMonth);
		}

		@Property
		void betweenSame(@ForAll("monthsBetweenSame") Month month) {
			assertThat(month).isEqualTo(startMonth);
		}

		@Provide
		Arbitrary<Month> monthsBetweenSame() {
			startMonth = DefaultMonthArbitrary.getMonthFromInt(Arbitraries.integers().between(1, 12).sample());
			endMonth = startMonth;
			return Dates.months().between(startMonth, endMonth);
		}

	}

}
