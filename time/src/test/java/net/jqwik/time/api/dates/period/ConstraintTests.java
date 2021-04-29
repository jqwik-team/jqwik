package net.jqwik.time.api.dates.period;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void defaultBetweenMinus1000And1000Years(@ForAll Period period) {
			assertThat(period.getYears()).isBetween(-1000, 1000);
		}

		@Property
		void range(@ForAll @PeriodRange(min = "P1Y2M", max = "P1Y5M3D") Period period) {
			assertThat(period.getYears()).isEqualTo(1);
			assertThat(period.getMonths()).isBetween(2, 5);
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void nonIsoPeriodThrowsException(@ForAll @PeriodRange(max = "13") Period period) {
		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void periodRange(@ForAll @PeriodRange(min = "P1Y2M", max = "P1Y5M3D") Byte b) {
			assertThat(b).isNotNull();
		}

	}

}
