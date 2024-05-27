package net.jqwik.time.api.dates.year;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void yearRangeBetweenMinus100And100(@ForAll @YearRange(min = -100, max = 100) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(-100);
			assertThat(year.getValue()).isLessThanOrEqualTo(100);
			assertThat((Object) year).isNotEqualTo(Year.of(0));
		}

		@Property
		void yearRangeBetween3000And3500(@ForAll @YearRange(min = 3000, max = 3500) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(3000);
			assertThat(year.getValue()).isLessThanOrEqualTo(3500);
		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void yearRange(@ForAll @YearRange(min = 500, max = 700) Float f) {
			assertThat(f).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void yearRange(@ForAll("years") @YearRange(min = 2022, max = 2025) Year year) {
			assertThat(year.getValue()).isBetween(2022, 2025);
		}

		@Provide
		Arbitrary<Year> years() {
			return of(
				Year.of(2021),
				Year.of(2022),
				Year.of(2023),
				Year.of(2024),
				Year.of(2025),
				Year.of(2026),
				Year.of(2027)
			);
		}

	}

}
