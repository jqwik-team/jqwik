package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class MonthTests {

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
		private Month[] allowedMonths;

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

		@Property
		void only(@ForAll("onlyMonths") Month month){
			assertThat(month).isIn(allowedMonths);
		}

		@Provide
		Arbitrary<Month> onlyMonths(){
			allowedMonths = generateMonths();
			return Dates.months().only(allowedMonths);
		}

	}

	public static Month[] generateMonths(){
		int count = Arbitraries.integers().between(1, 12).sample();
		Arbitrary<Month> monthArbitrary = Arbitraries.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER);
		ArrayList<Month> monthArrayList = new ArrayList<>();
		for(int i = 0; i < count; i++){
			Month toAdd = monthArbitrary.sample();
			monthArbitrary = monthArbitrary.filter(v -> !v.equals(toAdd));
			monthArrayList.add(toAdd);
		}
		return monthArrayList.toArray(new Month[]{});
	}

}
