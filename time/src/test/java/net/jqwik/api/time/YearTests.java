package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
class YearTests {

	@Property
	void validYearIsGenerated(@ForAll("years") Year year) {
		assertThat(year).isNotNull();
	}

	@Provide
	Arbitrary<Year> years() {
		return Dates.years();
	}

	@Group
	class CheckYearMethods {

		private int startInt;
		private int endInt;

		@Property
		void greaterOrEqual(@ForAll("yearsGreaterOrEqual") Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(startInt);
		}

		@Provide
		Arbitrary<Year> yearsGreaterOrEqual() {
			startInt = Arbitraries.integers().between(Year.MIN_VALUE, Year.MAX_VALUE).sample();
			return Dates.years().greaterOrEqual(startInt);
		}

		@Property
		void lessOrEqual(@ForAll("yearsLessOrEqual") Year year) {
			assertThat(year.getValue()).isLessThanOrEqualTo(endInt);
		}

		@Provide
		Arbitrary<Year> yearsLessOrEqual() {
			endInt = Arbitraries.integers().between(Year.MIN_VALUE, Year.MAX_VALUE).sample();
			return Dates.years().lessOrEqual(endInt);
		}

		@Property
		void between(@ForAll("yearsBetween") Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(startInt);
			assertThat(year.getValue()).isLessThanOrEqualTo(endInt);
		}

		@Provide
		Arbitrary<Year> yearsBetween() {
			startInt = Arbitraries.integers().between(Year.MIN_VALUE, Year.MAX_VALUE).sample();
			endInt = Arbitraries.integers().between(startInt, Year.MAX_VALUE).sample();
			return Dates.years().between(startInt, endInt);
		}

		@Property
		void betweenSame(@ForAll("yearsBetweenSame") Year year) {
			assertThat(year.getValue()).isEqualTo(startInt);
		}

		@Provide
		Arbitrary<Year> yearsBetweenSame() {
			startInt = Arbitraries.integers().between(Year.MIN_VALUE, Year.MAX_VALUE).sample();
			endInt = startInt;
			return Dates.years().between(startInt, endInt);
		}

	}

}
