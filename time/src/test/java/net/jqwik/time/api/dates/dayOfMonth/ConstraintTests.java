package net.jqwik.time.api.dates.dayOfMonth;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) int dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) Integer dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void dayOfMonthRange(@ForAll @DayOfMonthRange JqwikRandom random) {
			assertThat(random).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void dayOfMonthRange(@ForAll("integers") @DayOfMonthRange(min = 15, max = 19) Integer i) {
			assertThat(i).isBetween(15, 19);
		}

		@Provide
		Arbitrary<Integer> integers() {
			return of(14, 15, 16, 17, 18, 19, 20);
		}

	}

}
