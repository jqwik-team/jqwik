package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class PeriodTests {

	@Provide
	Arbitrary<Period> periods() {
		return Dates.periods();
	}

	@Property
	void validPeriodIsGenerated(@ForAll("periods") Period period) {
		assertThat(period).isNotNull();
	}

	@Property
	void validPeriodIsGeneratedWithAnnotation(@ForAll Period period) {
		assertThat(period).isNotNull();
	}

	@Group
	class PeriodMethods {

		@Property
		void yearsBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().yearsBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isBetween(start2, end2);
			});

		}

		@Property
		void yearsBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().yearsBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isEqualTo(start);
			});

		}

		@Property
		void monthsBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().monthsBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getMonths()).isBetween(start2, end2);
			});

		}

		@Property
		void monthsBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().monthsBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getMonths()).isEqualTo(start);
			});

		}

		@Property
		void daysBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().daysBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getDays()).isBetween(start2, end2);
			});

		}

		@Property
		void daysBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().daysBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getDays()).isEqualTo(start);
			});

		}

	}

	@Group
	class CheckEqualDistribution {

		@Property
		void periodNotAlways0(@ForAll Period period) {
			Statistics.label("Period not always 0")
					  .collect(period.isZero())
					  .coverage(coverage -> {
						  coverage.check(true).count(c -> c >= 1);
						  coverage.check(false).count(c -> c >= 900);
					  });
		}

		@Property
		void periodCanBePositiveAndNegative(@ForAll Period period) {
			Statistics.label("Period is negative")
					  .collect(period.isNegative())
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 40);
						  coverage.check(false).percentage(p -> p >= 40);
					  });
		}

	}

}
