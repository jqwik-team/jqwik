package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.time.copy.ArbitraryTestHelper.*;

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

		@Property
		void greaterOrEqual(@ForAll("years") Year year) {

			Arbitrary<Year> years = Dates.years().greaterOrEqual(year);

			assertAllGenerated(years.generator(1000), y -> {
				assertThat(y).isGreaterThanOrEqualTo(year);
			});

		}

		@Property
		void lessOrEqual(@ForAll("years") Year year) {

			Arbitrary<Year> years = Dates.years().lessOrEqual(year);

			assertAllGenerated(years.generator(1000), y -> {
				assertThat(y).isLessThanOrEqualTo(year);
			});

		}

		@Property
		void between(@ForAll("years") Year startYear, @ForAll("years") Year endYear) {

			Assume.that(startYear.compareTo(endYear) <= 0);

			Arbitrary<Year> years = Dates.years().between(startYear, endYear);

			assertAllGenerated(years.generator(1000), year -> {
				assertThat(year).isGreaterThanOrEqualTo(startYear);
				assertThat(year).isLessThanOrEqualTo(endYear);
			});

		}

		@Property
		void betweenSame(@ForAll("years") Year year) {

			Arbitrary<Year> years = Dates.years().between(year, year);

			assertAllGenerated(years.generator(1000), y -> {
				assertThat(y).isEqualTo(year);
			});

		}

	}

}
